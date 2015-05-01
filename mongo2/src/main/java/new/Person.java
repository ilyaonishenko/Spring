package new;
public class Person{
	private String name;
	private String id;
	private int age;
	public Person(String name,int age){
		this.name = name;
		this.age=  age;
	}
	public String getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public int getAge(){
		return age;
	}
	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}
