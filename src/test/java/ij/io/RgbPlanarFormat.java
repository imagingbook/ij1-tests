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
public class RgbPlanarFormat extends PixelFormat {

	RgbPlanarFormat()
	{
		super("RgbPlanar",3,8,3);  // super(String name, int numSamples, int bitsPerSample, int planes)
	}
	
	@Override
	boolean canDoImageCombo(int compression, ByteOrder.Value byteOrder, int headerBytes, boolean stripped)
	{
		if (compression == FileInfo.COMPRESSION_UNKNOWN)
			return false;
		if (compression == FileInfo.JPEG)  // TODO: remove this restriction to test jpeg compression
			return false;
		
		if (byteOrder == ByteOrder.Value.INTEL)
			return false;

		if (stripped && (compression == FileInfo.COMPRESSION_NONE))
			return false;
		
		return true;
	}
	
	@Override
	byte[] nativeBytes(long pix, ByteOrder.Value byteOrder)
	{
		byte[] output = new byte[3];

		output[0] = (byte)((pix & 0x00ff0000) >> 16);
		output[1] = (byte)((pix & 0x0000ff00) >> 8);
		output[2] = (byte)((pix & 0x000000ff) >> 0);
		
		return output;
	}
	
	@Override
	byte[] getBytes(long[][] image, int compression, ByteOrder.Value byteOrder, int headerBytes, boolean inStrips, FileInfo fi)
	{
		initializeFileInfo(fi,FileInfo.RGB_PLANAR,compression,byteOrder,image.length,image[0].length);
		
		byte[] output = PixelArranger.arrangeAsPlanes(this, image, fi, inStrips, false);
		
		output = PixelArranger.attachHeader(fi,headerBytes,output);

		return output;
	}

	@Override
	Object expectedResults(long[][] inputImage)
	{
		int[] output = new int[inputImage.length * inputImage[0].length];
		
		// NOTICE input is rgb planar but output is argb
		
		int i = 0;
		for (long[] row : inputImage)
			for (long pix : row)
				output[i++] = (int)(0xff000000 | (pix & 0xffffff));

		return output;
	}

	@Override
	Object pixelsFromBytes(byte[] bytes, ByteOrder.Value order)
	{
		// this method not tested by ImageWriter. Therefore no implementation until it will be used.
		return null;
	}
}

