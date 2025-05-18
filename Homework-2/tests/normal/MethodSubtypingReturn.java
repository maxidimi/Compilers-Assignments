class MethodSubtypingReturn {
    public static void main(String[] args) {
        B b;
        A a;
        a = b.getSelf();
        b = new B();
    }
}
class A {
    public A getSelf() {
        return new B();
    }
}

class B extends A {
    public A getSelf() {
        return new A();
    }
}