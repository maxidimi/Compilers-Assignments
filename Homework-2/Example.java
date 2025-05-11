class Example {
    public static void main(String[] args) {
        System.out.println(A.foo(1, 2));
    }
}

class A {
    int i;
    A a;
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
        boolean[] k;
        k = new boolean[10];
        return i+j;
    }
    
    public int foobar(boolean k) {
        return 1;
    }
}

class C extends B {
    int i;
    int[] k;

    public int foo(int i, int j) {
        return i+j;
    }
    
    public int foobar(boolean k) {
        return 1;
    }

    public int[] threepar(int i, int j, int k, boolean[] l) {
        int[] m;
        m = new int[10];
        m[0] = i;
        m[1] = j;
        m[2] = k;

        return m;
    }
}