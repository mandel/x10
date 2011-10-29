/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/short_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>
#include <errno.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                           'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                           'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                           'x', 'y', 'z' };

const ref<String> x10aux::short_utils::toString(x10_short value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    if (radix < 2 || radix > 36) radix = 10;
    // worst case is binary of Short.MIN_VALUE -- - plus 16 digits and a '\0'
    char buf[18] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_short value2 = value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[17] ; value2>0 ; value2/=radix) {
        *(--b) = numerals[::abs((int)(value2 % radix))];
    }
    if (value < 0) {
        *(--b) = '-';
    }
    return String::Steal(alloc_printf("%s",b));
}

const ref<String> x10aux::short_utils::toString(x10_short value) {
    return to_string(value);
}

const ref<String> x10aux::short_utils::toString(x10_ushort value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    if (radix < 2 || radix > 36) radix = 10;
    // worst case is binary: 16 digits and a '\0'
    char buf[17] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_ushort value2 = value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[16] ; value2>0 ; value2/=radix) {
        *(--b) = numerals[value2 % radix];
    }
    return String::Steal(alloc_printf("%s",b));
}

const ref<String> x10aux::short_utils::toString(x10_ushort value) {
    return to_string(value);
}

x10_short x10aux::short_utils::parseShort(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_int ans = strtol(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_short)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_short)ans;
}

x10_ushort x10aux::short_utils::parseUShort(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_uint ans = strtoul(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_ushort)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_ushort)ans;
}

x10_short x10aux::short_utils::reverseBytes(x10_short x) {
    x10_ushort ux = (x10_ushort)x;
    x10_ushort b0 = ux & 0x0F;
    x10_ushort b1 = (ux & 0xF0) >> 8;
    ux = (b0 << 8) | b1;
    return (x10_short)ux;
}
// vim:tabstop=4:shiftwidth=4:expandtab
