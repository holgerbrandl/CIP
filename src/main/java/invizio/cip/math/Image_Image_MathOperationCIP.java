package invizio.cip.math;


import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import invizio.cip.CIP;
import net.imagej.ImageJ;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * 
 * @author Benoit Lombardot
 *
 */


	
	@Plugin(type = CIP.MathBinary.class, name="Image_Image_MathOperationCIP", headless = true)
	public class Image_Image_MathOperationCIP  < T extends RealType<T> > extends AbstractOp 
	{
		
		@Parameter (type = ItemIO.INPUT, persist=false)
		private  String OperationType;
		
		@Parameter (type = ItemIO.INPUT, persist=false)
		private RandomAccessibleInterval<T> inputImage1;
		
		@Parameter (type = ItemIO.INPUT, persist=false)
		private RandomAccessibleInterval<T> inputImage2;
		

		@Parameter (type = ItemIO.OUTPUT)
		private RandomAccessibleInterval<T> outputImage;

		@Parameter
		private OpService opService;
		
		@Override
		public void run() {
			
			// check input parameters
			
			if ( inputImage1 == null || inputImage2 == null ){
				//TODO: Error! no image was provided
				return;
			}
			
			int nDim1 = inputImage1.numDimensions();
			long[] dims1 = new long[nDim1];
			inputImage1.dimensions(dims1);
			
			int nDim2 = inputImage2.numDimensions();
			long[] dims2 = new long[nDim2];
			inputImage1.dimensions(dims2);
			
			if ( nDim1!=nDim2 ) {
				// TODO: error image should have the same number of dimensions
				return;
			}

			for( int d=0; d<nDim1 ; d++ )
				if( dims1[d]/dims2[d] != 1 )
					// TODO: error image should have the same number of dimensions
					return;
			
			

			
			IterableInterval<T> in1Iter = Views.iterable(inputImage1);
			IterableInterval<T> in2Iter = Views.iterable(inputImage2);
			
			if( in1Iter.iterationOrder().equals( in2Iter.iterationOrder() ) )
			{
				outputImage = (Img<T>) opService.run("math."+OperationType, in1Iter, in2Iter );
			}
			else
			{
				outputImage = (Img<T>) opService.run("math."+OperationType, in1Iter, inputImage2 );
			}
			
			
			
		}

		
		
		public static void main(final String... args)
		{
			
			ImageJ ij = new ImageJ();
			ij.ui().showUI();
			
			ImagePlus imp = IJ.openImage(	CIP.class.getResource( "/blobs32.tif" ).getFile()	);
			ij.ui().show(imp);
			
			
			Img<FloatType> img = ImageJFunctions.wrap(imp);
			Img<IntType> img2 = ij.op().convert().int32( img );
			
			CIP cip = new CIP();
			cip.setContext( ij.getContext() );
			cip.setEnvironment( ij.op() );
			//@SuppressWarnings("unchecked")
			RandomAccessibleInterval<?> sumImg = (RandomAccessibleInterval<?>)
						cip.add( img , img2  );
			
			String str = sumImg==null ? "null" : sumImg.toString();
			
			System.out.println("hello addImage:" + str );
			ij.ui().show(sumImg);
			
			System.out.println("done!");
		}
		
	
}
