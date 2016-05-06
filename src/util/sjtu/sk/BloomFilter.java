package util.sjtu.sk;

import java.util.BitSet;

/**
 * Naive implementation of Bloom Filter
 * Note that the operations to Bloom Filter are not thread secure
 * @author ShenKai
 *
 * @param <T> : Data type of element
 */
public class BloomFilter<T> {
	private class SimpleHash<T> {   
	    private int cap;  
	    private int seed;  
	  
	    public SimpleHash(int cap, int seed) {  
	        this.cap = cap;  
	        this.seed = seed;  
	    }  
	  
	    public int hash(T value) { 
	    	String str = value.toString();
	        int result = 0;  
	        int len = str.length();  
	        for (int i = 0; i < len; i++) {  
	            result = seed * result + str.charAt(i);  
	        }  
	        return (cap - 1) & result;  
	    }  
	}

    private int DEFAULT_SIZE; // length of bloom filter
    private static final int[] seeds = {3, 5, 7, 11, 13, 31, 37, 61};
    private BitSet bits = null;  
    private SimpleHash<T>[] func = new SimpleHash[seeds.length];  
  
    public BloomFilter(int size) {
    	this.DEFAULT_SIZE = size;
    	bits = new BitSet(DEFAULT_SIZE);  	
    	for(int i = 0; i < seeds.length; i++) {  
            func[i] = new SimpleHash<T>(DEFAULT_SIZE, seeds[i]);  
        } 
    }
    
    public void addElement(T value) {
    	if(value == null)
    		return;
        for(SimpleHash<T> f : func)
            bits.set(f.hash(value), true);  
    }  
      
    public boolean contains(T value) {  
        if(value == null) 
        	return false;  
        boolean ret = true;  
        for(SimpleHash<T> f : func)
            ret = ret && bits.get(f.hash(value));  
        return ret;  
    }  
      
    public static void main(String[] args) {  
        String v1 = "xkeyideal@gmail.com";  
        String v2 = "www.baidu.com";
        String v3 = "www.google.com";
        String v4 = "www.sina.com";
        String v5 = "www.sjtu.edu.cn";
        
        BloomFilter<String> bf = new BloomFilter<String>(2<<24); 
        bf.addElement(v1);
        bf.addElement(v4);
        bf.addElement(v5);
        
        System.out.println(bf.contains(v1)); 
        System.out.println(bf.contains(v2));  
        System.out.println(bf.contains(v3));  
        System.out.println(bf.contains(v4));  
        System.out.println(bf.contains(v5));  
    }  

  
	
}
