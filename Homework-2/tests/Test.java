class BadAssign {

    public static void main(String[] args){
        A a;
        int A;
        int i;
        a = new A();
        i = A.getX();
    }

}


class A {

    int x;

    public int getX(){
        return x;
    }

}


class B extends A {

    int x;

    public int getX(){
        x = 1;
        return x;
    }

}
