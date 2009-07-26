/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoClient;
import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.net.NetworkHotpotatoClient;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Customer implements ExecutorService {
    private HotpotatoClient client;
    private Map<String, Future<?>> trackedFutures;

    public Customer(InetAddress address, int orderPort) {
        this(new NetworkHotpotatoClient(address, orderPort));
    }

    public Customer(HotpotatoClient client) {
        this.client = client;
        this.trackedFutures = new HashMap<String, Future<?>>();
    }

    public void execute(Runnable command) {
        try {
            SerializableCallable order = new SerializableCallable(command);
            placeOrder(null, order);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Future<?> submit(Runnable task) {
        return submit(new SerializableCallable(task));
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return submit(new WrappedRunnable<T>(task, result));
    }

    public <T> Future<T> submit(Callable<T> callable) {
        UUID uuid = UUID.randomUUID();
        Callable<? extends Serializable> order = cast(callable);
        String orderId;
        try {
            orderId = placeOrder(uuid.toString(), order);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Future<T> future = new Future<T>(orderId);
        synchronized (trackedFutures) {
            trackedFutures.put(orderId, future);
        }
        return future;
    }

    @SuppressWarnings("unchecked")
    private <T> Callable<? extends Serializable> cast(Callable<T> callable) {
        return (Callable<? extends Serializable>) callable;
    }

    public String placeOrder(String prefix,
            final Callable<? extends Serializable> order) throws IOException {
        return "" + client.send(new PlaceOrderRequest(prefix, order));
    }

    public Serializable pickupOrder(String id) throws IOException {
        Serializable result = client.send(new PickUpOrderRequest(id));
        if (result != null) {
            synchronized (trackedFutures) {
                Future<?> future = trackedFutures.get(id);
                if (future != null) {
                    future.result = result;
                }
            }
        }
        return result;
    }

    public boolean cancelOrder(final String id) throws IOException {
        Serializable response = client.send(new CancelOrderRequest(id));
        synchronized (trackedFutures) {
            Future<?> future = trackedFutures.remove(id);
            if (future != null) {
                future.isCancelled = true;
            }
        }
        return ((Boolean) response).booleanValue();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public <T> List<java.util.concurrent.Future<T>> invokeAll(
            Collection<Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public <T> List<java.util.concurrent.Future<T>> invokeAll(
            Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout,
            TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        throw new UnsupportedOperationException();
    }

    public boolean isShutdown() {
        throw new UnsupportedOperationException();
    }

    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    public long millisToPause() {
        return 5 * 1000;
    }

    public class Future<T> implements java.util.concurrent.Future<T> {
        private final String orderId;
        private boolean isCancelled;
        private Serializable result;

        Future(String orderId) {
            this.orderId = orderId;
            this.isCancelled = false;
            this.result = null;
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public boolean isDone() {
            return result != null;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            try {
                boolean b = cancelOrder(orderId);
                return b;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public T get() throws InterruptedException, ExecutionException {
            while (isCancelled() == false) {
                try {
                    pickupOrder(orderId);
                    if (result != null) {
                        return castResult();
                    }
                    Thread.sleep(millisToPause());
                } catch (IOException ioe) {
                    throw new ExecutionException(ioe);
                }
            }
            throw new CancellationException();
        }

        @SuppressWarnings("unchecked")
        private T castResult() {
            return (T) result;
        }

        public T get(long timeout, TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {

            class Getter extends Thread {
                T gotten = null;
                Exception caught = null;

                public void start() {
                    try {
                        gotten = get();
                    } catch (Exception e) {
                        caught = e;
                    }
                }

                private void throwIfNeeded() throws InterruptedException,
                        TimeoutException, ExecutionException {
                    if (caught != null) {
                        if (caught instanceof RuntimeException) {
                            throw (RuntimeException) caught;
                        }
                        if (caught instanceof InterruptedException) {
                            throw (InterruptedException) caught;
                        }
                        if (caught instanceof TimeoutException) {
                            throw (TimeoutException) caught;
                        }
                        if (caught instanceof ExecutionException) {
                            throw (ExecutionException) caught;
                        }
                        throw new RuntimeException(caught);
                    }
                }
            }

            Getter g = new Getter();
            g.start();
            g.join(unit.toMillis(timeout));
            g.throwIfNeeded();
            return g.gotten;
        }

    }

    public static class PlaceOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private Callable<? extends Serializable> order;
        private String id;

        public PlaceOrderRequest(String id,
                Callable<? extends Serializable> order) {
            this.order = order;
            this.id = id;
        }

        public Serializable exec(HotpotatoServer restaurant) {
            return restaurant.takeOrder(id, order);
        }
    }

    public static class PickUpOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;

        public PickUpOrderRequest(String id) {
            this.id = id;
        }

        public Serializable exec(HotpotatoServer restaurant) {
            return restaurant.pickUpOrder(id);
        }
    }

    public static class CancelOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;

        public CancelOrderRequest(String id) {
            this.id = id;
        }

        public Boolean exec(HotpotatoServer restaurant) {
            Ticket popTheJob = restaurant.getTicket(id);
            boolean wasWaiting = popTheJob != null;
            return Boolean.valueOf(wasWaiting);
        }
    }

}
