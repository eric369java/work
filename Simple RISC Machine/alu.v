module ALU(Ain, Bin, ALUop, out, status);
  input [15:0] Ain, Bin;
  input [1:0] ALUop;
  output [15:0] out;
  reg [15:0] out;
  wire subovf; 
  wire addovf; 
  output [2:0] status;
  // Executes a given operation on Ain and Bin depending on ALUop
  // The four operations correspond to the ADD, CMP, AND, and MVN instructions, in that order.
  always @(*) begin
    case (ALUop)
	  2'b00: out = Ain + Bin;
	  2'b01: out = Ain - Bin;
	  2'b10: out = Ain & Bin;
	  2'b11: out = ~Bin;
	  default: out = {16{1'bx}};
	endcase
  end

  //subtraction overflow occurs if A and B are different signed, and B has the same sign as the output. 
  assign subovf = (ALUop == 2'b01) & (Ain[15] !== Bin[15]) & (out[15] == Bin[15]); 

  //addition overflow occurs.
  assign addovf = (ALUop !== 2'b01) & (Ain[15] == Bin[15]) && (out[15] !== Ain[15]);

  // Three bits of status in order: Z (zero flag), V (overflow flag), and N (negative flag) 
  assign status = {(out == 16'b0), addovf ^ subovf, (out[15] == 1'b1)};
  
endmodule