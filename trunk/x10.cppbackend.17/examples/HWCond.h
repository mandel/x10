#ifndef __HWCOND_H
#define __HWCOND_H

#include <x10lang.h>
using namespace x10::lang;
class HWCond : public Object {
public:
    static void main(x10::ref<x10::array<x10::ref<String> > > args);
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
