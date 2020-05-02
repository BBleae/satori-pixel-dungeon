package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	
	// Files
	
	public static boolean fileExists( String name ){
		FileHandle file = Gdx.files.local(name);
		return file.exists() && !file.isDirectory();
	}
	
	public static boolean deleteFile( String name ){
		return Gdx.files.local(name).delete();
	}
	
	// Directories
	
	public static boolean dirExists( String name ){
		FileHandle dir = Gdx.files.local( name );
		return dir.exists() && dir.isDirectory();
	}
	
	public static boolean deleteDir( String name ){
		FileHandle dir = Gdx.files.local( name );
		
		if (dir == null || !dir.isDirectory()){
			return false;
		} else {
			return dir.deleteDirectory();
		}
	}
	
	// bundle reading
	
	//only works for base path
	public static Bundle bundleFromFile( String fileName ) throws IOException{
		FileHandle file = Gdx.files.local(fileName);
		if (!file.exists()){
			throw new FileNotFoundException("file not found: " + file.path());
		} else {
			return bundleFromStream(file.read());
		}
	}
	
	private static Bundle bundleFromStream( InputStream input ) throws IOException{
		Bundle bundle = Bundle.read( input );
		input.close();
		return bundle;
	}
	
	// bundle writing
	
	//only works for base path
	public static void bundleToFile( String fileName, Bundle bundle ) throws IOException{
		try {
			bundleToStream(Gdx.files.local(fileName).write(false), bundle);
		} catch (GdxRuntimeException e){
			if (e.getCause() instanceof IOException){
				//we want to throw the underlying IOException, not the GdxRuntimeException
				throw (IOException)e.getCause();
			} else {
				throw e;
			}
		}
	}
	
	private static void bundleToStream( OutputStream output, Bundle bundle ) throws IOException{
		Bundle.write( bundle, output );
		output.close();
	}

}
