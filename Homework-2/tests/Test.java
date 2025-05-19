class Test {
    public static void main(String[] args) {
        //System.out.println(A.foo(1, 2));
    }
}

class A {
    int i;
    A a;
    int[] arr;
    boolean[] b;

    public int foo(int i, int j) {
        int k;
        k = i+j;
        k = this.foo(i, j);

        return k; 
    }
}

class B extends A {
    boolean i;

    public boolean foo(int i, int j) {
        int k;
        k = i+j;
        //k = this.foo(i, j);

        //return k; 
        return true;
    }
}
