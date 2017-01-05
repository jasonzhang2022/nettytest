package jason.example.protobuf;

import java.util.Date;

import io.protostuff.Tag;

public class User {

	@Tag(1)
	String firstName;
	@Tag(2)
	String lastName;
	@Tag(3)
	int age;
	@Tag(4)
	Date birthDate;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + ", birthDate=" + birthDate
				+ "]";
	}
	
	
}
