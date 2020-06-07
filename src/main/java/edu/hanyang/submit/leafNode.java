package edu.hanyang.submit;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class leafNode extends Node {
leafNode(byte[] tree_buffer, int blocksize, int status, int offset) {
		
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
		ByteBuffer bf = ByteBuffer.wrap(tree_buffer);
		for(int i = 0; i < tree_buffer.length/8; i++) {
			this.vals.add(bf.getInt());
			this.keys.add(bf.getInt());
		}
	}
	
	leafNode(byte[] tree_buffer, int blocksize, byte[] meta_buffer) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		ByteBuffer bf = ByteBuffer.wrap(meta_buffer);
		this.status = bf.getInt();
		this.offset = bf.getInt();
		bf.clear();
		bf = ByteBuffer.wrap(tree_buffer);
		for(int i = 0; i < tree_buffer.length/8; i++) {
			this.vals.add(bf.getInt());
			this.keys.add(bf.getInt());
		}	
		
	}
	leafNode(int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
	}
	
	public void insert(int key, int val) {
        int low = 0;
        int high = this.keys.size();
        while (low < high) {
            final int mid = low + (high - low)/2;
            if (key >= this.keys.get(mid)) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        this.vals.add(low-1, val);
        this.keys.add(low, key);
	}
	
	public byte[] to_tree_buffer() {
		this.set_node_size();
		this.buffer = new byte[this.node_size*4];
		int i;
		for(i = 0; i < this.node_size / 2; i++) {
			intTobyte(this.vals.get(i), i*8);
			intTobyte(this.keys.get(i+1), 4+i*8);
		}
		return this.buffer;
	}
	public byte[] to_tree_buffer(int index) {
		this.node_size = 2*(index - 1);
		int num_buffer = this.keys.size() + this.vals.size() - this.node_size - 1;
		this.buffer = new byte[num_buffer*4];
		for(int i = 0; i < num_buffer/2 ; i++) {
			intTobyte(this.vals.get(index-1+i), i*8);
			intTobyte(this.keys.get(index+i), 4+i*8);
		}
		this.vals = this.vals.subList(0, index-1);
		this.keys = this.keys.subList(0, index);
		return this.buffer;
	}
	public Node copyNode(){
		return new leafNode(this.to_tree_buffer(), this.blocksize, this.to_meta_buffer());
	}
}
