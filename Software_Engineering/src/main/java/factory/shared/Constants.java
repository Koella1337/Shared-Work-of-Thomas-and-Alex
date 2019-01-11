package factory.shared;

public class Constants {
	
	public enum PlaceableSize {
		SHELF				(  6, 40),
		BOX					( 40, 40),
		
		FORKLIFT			( 10, 10),
		
		ASSEMBLY_LINE		(350,100),
		
		SHIPPING_BOX		(100,100),
		STAFF_QUARTERS		(400,100);
		
		public final int x, y;
		
		private PlaceableSize(int xSize, int ySize) {
			this.x = xSize;
			this.y = ySize;
		}
	}
	
	
}
