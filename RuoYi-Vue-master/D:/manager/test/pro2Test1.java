

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class pro2Test1 {

    @Test
    public void testpro2Test1() {
        pro2 obj = new pro2();
        try {
            assertEquals(false, obj.pro2(0, 1, 0));
        } catch (AssertionError e) {
            // 如果断言失败，输出失败信息
            System.out.println("Test failed: " + e.getMessage());
        }
    }
}
