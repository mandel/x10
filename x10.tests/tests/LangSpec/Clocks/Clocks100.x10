/* Current test harness gets confused by packages, but it would be in package Clocks_ClockedFinish;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class Clocks100 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Clocks100().execute();
    }


// file Clocks line 433
 static class Example2 {
static def before(Long) {}
static def after(Long) {}
def example(iters:long) {
clocked finish {
  for (p in Place.places()) {
     at (p) clocked async {
       for (count in 1..iters) {
         before(count);
         Clock.advanceAll();
         after(count);
       }
     }
  }
}
}}

 static class Hook {
   def run():Boolean = true;
}

}
