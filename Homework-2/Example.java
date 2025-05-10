class Example {
    public static void main(String[] args) {
        System.out.println(A.foo(1, 2));
    }
}

class A {
    int i;
    A a;

    public int foo(int i, int j) {
        int k;
        k = i+j;

        return k; 
    }

    public int bar() { 
        return 1;
    }
}

class B extends A {
    int i;

    public int foo(int i, int j) {
        return i+j;
    }
    
    public int foobar(boolean k) {
        return 1;
    }
}

class C extends B {
    int i;

    public int foo(int i, int j) {
        return i+j;
    }
    
    public int foobar(boolean k) {
        return 1;
    }
}