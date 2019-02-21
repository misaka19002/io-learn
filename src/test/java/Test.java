/**
 * Created by wyd on 2019/2/13 10:36:11.
 */
public class Test {
    
    @org.junit.Test
    public void s() {
        System.out.println(1 << 4);
        System.out.println(1 << 3);
        System.out.println(1 << 2);
        System.out.println(1 << 1);
        System.out.println(1 << 0);
    }
    
    @org.junit.Test
    public void ox() {
        System.out.println(0x00);
        System.out.println(0x01);
        System.out.println(0x02);
        System.out.println(0x04);
        System.out.println(0x05);
    }
    
    @org.junit.Test
    public void oxbyte() {
    
        byte d0 = 0x00;
        byte d5 = 0x05;
        
        byte d5a = 0x05 & 0xFFFF;
        
        
        byte[] bytes = new byte[]{0x05, 0x01};
        System.out.println(bytes);
        
        System.out.println(0x61);
        System.out.println((byte) 0x61);
        System.out.println((char) 0x61);
        
    }
}
