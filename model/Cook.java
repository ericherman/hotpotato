/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;

import java.io.*;
import java.net.*;

public class Cook {
    private volatile Status status;
    private volatile Throwable workerException;
    private volatile int ordersFilled;

    public Cook() {
        reset();
    }

    synchronized public void setKitchen(
        InetAddress restaurantAddress,
        int ticketWheelPort,
        int counterTopPort) {

        if (status == Status.NO_SERVER) {
            setStatus(Status.POLLING);
            Worker worker =
                new Worker(restaurantAddress, ticketWheelPort, counterTopPort);
            new Thread(worker, "KitchenWorker").start();
        }
    }

    final public void reset() {
        /*
         * referenced from the constructor -- don't remove the final
         * attribute without considering it carefully.
         */
        ordersFilled = 0;
        workerException = null;
        setStatus(Status.NO_SERVER);
    }

    synchronized void setProblem(Throwable t) {
        workerException = t;
        setStatus(Status.PROBLEM);
    }

    public Throwable getLastProblem() {
        return workerException;
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int ordersFilled() {
        return ordersFilled;
    }

    public static class Status {
        public static final Status POLLING = new Status("POLLING");
        public static final Status WORKING = new Status("WORKING");
        public static final Status NO_SERVER = new Status("NO_SERVER");
        public static final Status PROBLEM = new Status("PROBLEM");
        private String status;
        private Status(String status) {
            this.status = status;
        }
        public String toString() {
            return status;
        }
    }

    class Worker implements Runnable {
        private volatile InetAddress restaurantAddress;
        private volatile int ticketWheelPort;
        private volatile int counterTopPort;

        private Order work = null;

        Worker(
            InetAddress restaurantAddress,
            int ticketWheelPort,
            int counterTopPort) {

            this.restaurantAddress = restaurantAddress;
            this.ticketWheelPort = ticketWheelPort;
            this.counterTopPort = counterTopPort;
        }

        public void run() {
            try {
                while (Status.POLLING.equals(getStatus())) {
                    work = readFromTicketWheel();
                    setStatus(Status.WORKING);
                    if (work != null) {
                        work.exec();
                        sendToCounterTop();
                        ordersFilled++;
                    }
                    setStatus(Status.POLLING);
                    Thread.yield();
                }
            } catch (Throwable t) {
                setProblem(t);
            }
        }

        private Order readFromTicketWheel() throws IOException {
            Socket ticketWheel = new Socket(restaurantAddress, ticketWheelPort);
            return (Order) new ObjectReceiver(ticketWheel).receive();
        }

        private void sendToCounterTop() throws IOException {
            Socket counterTop = new Socket(restaurantAddress, counterTopPort);
            new ObjectSender(counterTop).send(work);
        }
    }
}
