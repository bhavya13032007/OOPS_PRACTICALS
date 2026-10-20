abstract class shape {
    abstract void area();

    void display() {
        System.out.println("This is a shape.");
    }
}
class circle extends shape {


    void area() {
        double r = 5;
        double area = 3.14 * r * r;
        System.out.println("Area of circle: " + area);
    }
}
class rectangle extends shape {
    void area() {
        double length = 10;
        double breadth = 5;
        double area = length * breadth;
        System.out.println("Area of rectangle: " + area);
    }
}
class triangle extends shape {
    void area() {
        double base = 8;
        double height = 6;
        double area = 0.5 * base * height;
        System.out.println("Area of triangle: " + area);
    }
}
public class main {
    public static void main(String[] args){
        
        shape s1 = new circle();
        s1.display();
        s1.area();

        shape s2 = new rectangle();
        s2.display();
        s2.area();

        shape s3 = new triangle();
        s3.display();
        s3.area();
    }

}