Copyright (C) 2003 by Eric Herman.
 
This file contains a few notes about the hotpotato codebase. 
It's current as of 2003/08/23.

LICENSING

The Hotpotato programs are free software; you can redistribute 
them and/or modify them under the terms of the GNU General Public 
License as published by the Free Software Foundation; either 
version 2 of the License, or (at your option) any later version.

These programs are distributed in the hope that they will be 
useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
(GnuGeneralPublicLicenseVersion2.txt) along with these programs; 
if not, write to:

the Free Software Foundation, Inc.
59 Temple Place, Suite 330
Boston, MA  02111-1307  USA

The GPL may not suit some users. No problem, feel free to contact 
Eric Herman by email <eric AT rnd DOT cx> for alternative 
licensing, I'm sure we can work something out. 

SUMMARY

The Hotpotato code is a framework for "grid computing" or 
whatever the current buzzword is for distributed processing. The
idea is that you set up a server, allow other users to connect
to the server as clients, and then give the server work to do. 
The server then hands work out to the clients.

The goal of Hotpotato is to make all of the aspects of the 
distribution of work easy. Hopefully the only hard part will be 
in writing the actual work to get distributed! With an easy 
distribution framework available, work which we might normally 
batch in serial may make more sense in parallel. Of course, 
not all problems lend themselves to parallel solutions. 

THANKS

The idea was first given to me by Ian Greenhoe, so he could 
distribute the work of a project of his. He is a huge help 
and inspiration for the project. (It always helps to know that
someone is finding it useful.) He also gave me a hand thinking 
through the initial designs and writing the initial test code, 
as well as helping me debug various stuff along the way.

The largest thanks goes to Brett Neumeier for everything, but 
mostly for being The Guru. Countless times I got stuck in the 
persnickety details of things like object stream headers or 
class loading or security management .... At least once, I was
so badly stuck, that I think I would have given up without his 
willingness to help me understand /why/ something was behaving 
unexpectedly. All the Googling in the world can't beat a 
friendly guru.
  
I spell very poorly and, until I get an IDE with a spell checker 
plug in (hint, hint!), I expect I will be continually grateful 
to the code gnomes that come around fixing my spelling errors. 
Thanks little guys!

Oh, and thank you for reading this far.

DEPENDENCIES

The hotpotato code has a few external dependencies, but these
dependencies are upon open source code.  This property of the 
codebase will be maintained. Knowledge is power.

At the time of this writing, Hotpotato depends on:
 * JUnit   http://junit.org/
 * BCEL    http://jakarta.apache.org/bcel/

The hotpotato codebase compiles using Sun's JDK 1.4.2 on linux 
... and probably any 1.4.x ... but I haven't done the testing. 

TESTING

Speaking of testing, all the code has been written test-first, 
so there should be no untested code. If there is code that can 
be removed without causing tests to break, it should probably 
be removed.

I try to make sure that any time I believe there is a bug, that 
first I write a failing test. Twice on this code I've been 
surprised that things haven't actually needed "fixing".

Oh, and be warned: there are some potentially bad race conditions 
in the tests. In time, I hope to address these race conditions, 
but that has not yet been done. Besides, they never break on MY
computer, anyway. <grin> In reality, I think I'll be motivated 
to fix the race conditions as a way to speed up the tests. 

However, because the tests do things in multiple threads, I must 
sheepishly admit to using Thread.sleep(x) in order to hopefully 
get the threads to line up sensibly in their orders of execution. 

If you have trouble with tests failing, try increasing the delay 
in hotpotato.io.ConnectionServer.SLEEP_DELAY.

I would like to implement a "No Sleeps!" policy in the tests so 
they would run fast. The 17 seconds it currently takes, I find 
rather slow. The slower they are, the less likely I will be to 
run them after making a small change, and the more confused I 
will be when later I run them and I discover that something I did 
broke something else. For some reason, I find it harder to revert 
half an hour's worth of code than I do reverting 5 to 10 minutes 
worth of coding 4 or 5 times in a row. For this reason I want it 
to be easy and painless to run the tests early and often. In the 
extreme, maybe I'd hook up the test running to my save key. :-)

SYSTEM OF NAMES - Packages, Classes, Variables

For starters, why "Hotpotato" anyway? Well, because when Ian and 
I first wrote some code to pass objects around between two or 
three socket connections, it felt like we were playing a virtual 
game of hotpotato. I'm sort of looking for a better name, but 
I've had a hard time coming up with one, and this one is sort of 
fun, even if not very informative.

I've embraced the restaurant "metaphor" for my model classes.
Several times I've thought about what would be a better set of 
names, with no luck. So far, the naming has been more helpful 
than I would have guessed. Restaurants are easy to understand, 
and naming things in line with a Restaurant has helped me keep 
straight which parts are supposed to be doing what things. 

On a few occasions, I made design decisions in part on how well 
the outcome would fit the names. Also, I've learned to re-work 
anything I can not find a good name for. This turns out to be 
really enlightening; maybe it is a coincidence, but when the code 
is hard to name, it is usually doing too much or, for some other 
reason, it is poorly organized.

Other times, I've had to settle for a little bit of "metaphor
shear" as Brett calls it. For instance, most restaurants don't 
have some variable number of free-lance cooks willing to work out 
of their garage. The imperfect fit keeps nudging me to keep an 
eye out for a better system of names. Please let me know if you 
can think of one.

CODING STYLE 

If you wish to contribute, thank you, that would be great. 
Please respect the following guidelines.

Test Test Test. Everything must be exercised in an automated 
JUnit test. Okay, not everything. You don't have to test the 
toString() method if it is just for viewing in the debugger. 
But if you're writing an equals method, go ahead and throw in a 
quick assertEquals(expected.hashCode(), actual.hashCode()); in
your test ... it's just one extra line to keep us honest. (Brett
says that, when he's feeling paranoid, he tests the entire equals
contract, including symmetry, reflexivity, transitivity, and
consistency checks.)

Keep it simple. I have to be able to understand what is going on
so I keep it simple. Often when I first add a test, and the 
feature, the code is complex. But when I get the green bar, I 
try to re-work the code to a more simple form. Most of the time 
it is weeks later when a vastly simpler form takes shape in the
code. But, so far, with each new feature, the code has been
getting more simple, not more complex. It usually seems to take
me as much or more time to make the working code simple as it
took to write the test and make it work in the first place.
Also, working in a completely unrelated area of code will inspire
an insight as to how to make something more simple. It's not a
science.

I avoid use of "static" unless I have a really compelling reason
not to. This is because, in Java, static is sometimes surprising.
For instance, static members are not truly global within the VM;
they are scoped by the ClassLoader through which they are loaded.
Static methods tend to be handy, except that they are hard to
mock-out for testing. Static methods aren't polymorphic, and just
aren't very "objecty" ... For these reasons, I tend to view the
use of "static" as a hint that I should be thinking about design
more.

I don't use null most of the time. In Java, null isn't an object
and it shows. Besides the NullPointerException being a pain in
the rump, it seems like a lot of duplication to check for a null
response from a method call before doing something with the
response. If I need a null-like value, I'd rather use a
"NullObject" which can be defined to throw exceptions, or do the
"no-op" behavior if I wish. Here's the rule of thumb I use: If I
have to check for null to avoid a legal situation (not a
programmer error) throwing a null pointer exception, then I
should be using a NullObject.

For code in the Hotpotato package, lines shall not exceed the
natural 80 columns that God intended ... unless you have a very,
very compelling reason. If more than three levels of indentation
are required, perhaps this is a clue that a new method is wanting
to be extracted.

Please use curly braces, even when they aren't strictly needed.
Why? Sigh. Because I'm asking nicely. I just like to be able to
drop an assertEquals() or println() in anywhere without having to
add (and later remove) the curly braces.

Also, since I do development work in Eclipse, I rely upon the 
code formatter built therein. Here are some of my settings:
  Java - Code Formatter
  * New Lines tab: deselect all check boxes except the last one
    (insert new line inside empty catch block)
  * Style tab: deselect insert tab for indentation, not spaces.
  * Style tab: number of spaces representing indent: 4
  Java - Editor
  * Appearance: Displayed tab width 8
  

Cheers!  
-- Eric Herman
