class Main {
	public static void main(String[] a) {}
}

class Base {
	int data;
	public int get(int y) {
		return data;
	}
}

class Derived extends Base {
    int i;
	public int get(int x) {
        i = 12;
        if (i < 12) {
            i = A.get(x);
        } else {
            i = 0;
        }

		return x;
	}
}
