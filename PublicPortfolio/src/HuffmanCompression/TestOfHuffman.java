package HuffmanCompression;
public class TestOfHuffman {

	public static void main(String[] args) {
		
		Huffman.encode("Nickelback-Photograph.txt", "pg_codes.txt", "NickelBack-Compressed.txt");
		Huffman.decode("NickelBack-Compressed.txt", "pg_codes.txt", "decomp_Nickelback-PG.txt");
	}

}
