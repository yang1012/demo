package yang_demo;

/**
 * @ClassName: Test
 * @package yang_demo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Mr.yang
 * @date 2016年1月8日 下午10:01:58
 * @version V1.0
 */
public class Test {
	public static void main(String args[]) {

		// 工资
		int salary = 25000;
		// 征税起点
		int startMoney = 3500;
		// 四金
		int other = 500;
		// 个人所得税
		int tax = 0;
		// 25000对应梯度税率
		float tax_rate = 0.25f;
		// 25000对应梯度扣减数
		int tidu = 1005;
		// 全月应纳税所得额
		int moeny = salary - startMoney - other;
		System.out.println(moeny * tax_rate - tidu);
	}
}
