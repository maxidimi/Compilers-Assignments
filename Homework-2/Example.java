class Example {
    public static void main(String[] args) {
        System.out.println(A.foo(1, 2));
    }
}

class A {
    int i;
    int[] l;
    A a;

    public int foo(int i, int j) {
        int k;
        {
            k = l.length;
        }
        k = l[1];

        return k; 
    }

    public int bar() { 
        return 1;
    }

    public int[] retArr() {
        int[] k;
        k = new int[1+1];
        k[0] = 1;
        k[1] = 2;

        i = A.foo(1, 2);

        return k;
    }
}

class B extends A {
    int i;

    public int foo(int i, int j) {
        boolean[] k;
        k = new boolean[1+1];
        return 1;
    }
    
    public int foobar(boolean k) {
        return 1;
    }
}

class C extends B {
    int i;
    int[] k;

    public int foo(int i, int j) {
        return 2;
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