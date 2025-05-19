class NewOnPrint {
    public static void main(String[] args) {
        //A A;
        System.out.println(new A().foo(1, 2));
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
}
