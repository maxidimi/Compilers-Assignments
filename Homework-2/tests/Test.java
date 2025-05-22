class Test {
    public static void main(String[] args) {
        //System.out.println(A.foo(1, 2));
    }
}

class A {
    int i;
    A a;

    public int foo(int i, int j) {
        int k;
        k = i+j;
        k = this.foo(i, j);
        k = new int[10].length;

        return k; 
    }
}

class B extends A {
    boolean i;

    public int getI() {
        return i;
    }
}
