

public class ComputationalIntensive {

	public ComputationalIntensive() {
		// TODO Auto-generated constructor stub
	}
	public Data refreshAllData(){
		try {
			Thread.sleep(9000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Data();
	}
	public void computeMonthlyStatistics(){
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
