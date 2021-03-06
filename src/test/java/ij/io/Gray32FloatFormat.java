/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package ij.io;

/**
 * TODO
 *
 * @author Barry DeZonia
 */
public class Gray32FloatFormat extends PixelFormat {

	Gray32FloatFormat()
	{
		super("Gray32Float",1,32,1);  // super(String name, int numSamples, int bitsPerSample, int planes)
	}
	
	@Override
	boolean canDoImageCombo(int compression, ByteOrder.Value byteOrder, int headerBytes, boolean stripped)
	{
		if (compression != FileInfo.COMPRESSION_NONE)
			return false;
		
		if (stripped)
			return false;
		
		return true;
	}
	
	@Override
	byte[] nativeBytes(long pix, ByteOrder.Value byteOrder)
	{
		byte[] output = new byte[4];
		
		float fPix = pix;

		int bPix = Float.floatToIntBits(fPix);
		
		output[0] = (byte)((bPix & 0xff000000) >> 24);
		output[1] = (byte)((bPix & 0x00ff0000) >> 16);
		output[2] = (byte)((bPix & 0x0000ff00) >> 8);
		output[3] = (byte)((bPix & 0x000000ff) >> 0);
		
		if (byteOrder == ByteOrder.Value.INTEL)
			PixelArranger.reverse(output);

		return output;
	}
	
	@Override
	byte[] getBytes(long[][] image, int compression, ByteOrder.Value byteOrder, int headerBytes, boolean inStrips, FileInfo fi)
	{
		initializeFileInfo(fi,FileInfo.GRAY32_FLOAT,compression,byteOrder,image.length,image[0].length);
		
		byte[] output;
		
		// ALWAYS contiguous in this case
		output = PixelArranger.arrangeContiguously(this,image,fi);
		
		output = PixelArranger.attachHeader(fi,headerBytes,output);

		return output;
	}

	@Override
	Object expectedResults(long[][] inputImage)
	{
		float[] output = new float[inputImage.length * inputImage[0].length];
		
		int i = 0;
		for (long[] row : inputImage)
			for (long pix : row)
				output[i++] = pix;
		return output;
	}

	@Override
	Object pixelsFromBytes(byte[] bytes, ByteOrder.Value order)
	{
		int numFloats = bytes.length / 4;
		float[] output = new float[numFloats];
		for (int i = 0; i < numFloats; i++)
		{
			int theInt;
			
			if (order == ByteOrder.Value.INTEL)
				theInt = ((bytes[4*i+3] & 0xff) << 24) | ((bytes[4*i+2] & 0xff) << 16) | ((bytes[4*i+1] & 0xff) << 8) | ((bytes[4*i+0] & 0xff) << 0);
			else
				theInt = ((bytes[4*i+0] & 0xff) << 24) | ((bytes[4*i+1] & 0xff) << 16) | ((bytes[4*i+2] & 0xff) << 8) | ((bytes[4*i+3] & 0xff) << 0);
			output[i] = Float.intBitsToFloat(theInt);
		}
		return output;
	}
}

