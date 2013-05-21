�û�ָ��
------
Guava ��Ŀ�������� Google Ա���� Java ��Ŀ���õ��ĺ��Ŀ�: ����, ����, ԭ��֧��, ������, ����ע��, �ַ�������, I/O, �ȵ�. Google ��Ա����ÿ�춼������������ʹ������Щ����.

�鿴 Javadoc ������ѧϰ���ʹ����Щ�������Ч�ķ�ʽ, ����ֻ�Ƕ� Guava �����к���ǿ���һЩ��������Щ˵��.

<i>This wiki is a work in progress, and parts of it may still be under construction.</i>

+ <b>����������</b>: Make using the Java language more pleasant.

    - <b>ʹ�úͱ��� null</b>: null ����������, ��������˷ѽ�Ĵ���, �������������˲�ˬ. �ܶ� Guava �Ĺ����������� null ʱ��ֱ�Ӿܾ������, ������ĬĬ�ؽ�������.

    - <b>ǰ������</b>: �����׵Ķ���ķ�������ǰ�������Ĳ���.

    - <b>�����Ķ��󷽷�</b>: ����Object���÷�����ʵ��, �� hashCode() �� toString().

    - <b>����</b>: Guava ǿ��� "fluent Comparator"�Ƚ���, �ṩ��ؼ�������.

    - <b>Throwable��</b>: �����쳣���ʹ��󴫲�.

+ <b>������</b>: Guava �� JDK ���������չ. ���� Guava ��Ŀ�����ƺ�Ϊ����֪�Ĳ���.
    - <b>Immutable collections</b>, �����Ա��, �����޸ĵļ���, and improved efficiency.
    
    - <b>New collection types</b>, for use cases that the JDK collections don't address as well as they could: multisets, multimaps, tables, bidirectional maps, and more.
    
    - <b>Powerful collection utilities</b>, java.util.Collections ��δ�����ĳ��ò���.
    
    - <b>Extension utilities</b>: �� Collection �������һ��װ����? ʵ�ֵ�����? ���и����׵ķ���.
    
+ <b>����</b>: �ֲ�����, done right, and supporting a wide variety of expiration behaviors.

+ <b>Functional idioms</b>: Used sparingly, Guava's functional idioms can significantly simplify code.

+ <b>Concurrency</b>: Powerful, simple abstractions to make it easier to write correct concurrent code.
    
    - <b>ListenableFuture</b>: Futures, with callbacks when they are finished.
    
    - <b>Service</b>: Things that start up and shut down, taking care of the difficult state logic for you.

+ <b>Strings</b>: һ���ǳ��ǳ����õ��ַ���������: �ṩ splitting, joining, padding �Ȳ���.

+ <b>Primitives</b>: ��չ JDK ��δ�ṩ�Ķ�ԭ�����ͣ���int��char�ȣ��Ĳ���, ����ĳЩ���͵��޷��ŵı���.

+ <b>Ranges</b>: Guava һ��ǿ��� API, �ṩ Comparable ���͵ķ�Χ����, ������������ɢ�����.

+ <b>I/O</b>: �� I/O ����, �ر��Ƕ� I/O �����ļ��Ĳ���, for Java 5 and 6.

+ <b>Hashing</b>: �ṩ�� Object.hashCode() �����ӵ� hash ����, �ṩ Bloom filters.

+ <b>EventBus</b>: ���ڷ���-����ģʽ�����ͨ�ţ����ǲ���Ҫ��ȷ��ע����ί�ж�����

+ <b>Math</b>: �Ż��� math �����࣬������������.

+ <b>Reflection</b>: Guava �� Java ������ƹ�����.

+ <b>Tips</b>: Getting your application working the way you want it to with Guava.
    - <b>Philosophy</b>: what Guava is and isn't, and our goals.
    - <b>Using Guava in your build</b>, with build systems including Maven, Gradle, and more.
    - <b>Using ProGuard</b> to avoid bundling parts of Guava you don't use with your JAR.
    - <b>Apache Commons equivalents</b>, helping you translate code from using Apache Commons Collections.
    - <b>Compatibility</b>, details between Guava versions.
    - <b>Idea Graveyard</b>, feature requests that have been conclusively rejected.
    - <b>Friends</b>, open-source projects we like and admire.