class CallArgumentSubtype {
    public static void main(String[] args) {
        A a;
        B b;
        int i;
        i = b.foo(b);
    }
}

class A { }

class B extends A {
    public int foo(A a) {
        return 0;
    }
}