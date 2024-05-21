


import org.junit.jupiter.api.Test;
import test.pro2;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class pro2Test3 {

    @Test
    public void testpro2Test3() {
        pro2 obj = new pro2();
        try {
            assertEquals(false, obj.pro2(1, 0, 0));
        } catch (AssertionError e) {
            // 如果断言失败，输出失败信息
            System.out.println("Test failed: " + e.getMessage());
        }
    }
}
