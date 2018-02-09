package cache.renj.com.cacheutils.test;

import java.io.Serializable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2018-02-09   14:46
 * <p>
 * 描述：用于缓存对象测试时使用
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class Person implements Serializable {
    private String name;
    private int age;
    private char sex;

    public Person() {
    }

    public Person(String name, int age, char sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "name = [" + name + "], age = [" + age + "], sex = [" + sex + "]";
    }
}
