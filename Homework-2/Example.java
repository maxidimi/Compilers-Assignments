class DoubleDeclaration1 {

    public static void main(String[] args) {}

}


class A {

    public A foo(A x){

        int[] a;
        boolean d;
        int b;
        d = true && false;
        b = a.length;
        b = a[(1+b)];

        return this;
    }


}
