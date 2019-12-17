module datapath(clk, anum, bnum, vsel, 
		shift, asel, bsel, ALUop, loadc, 
		loads, writenum, write, status_out, datapath_out, creg_out, sximm5, sximm8, PC, mdata);

input clk, loads, loadc, asel, bsel, write;
input [15:0] sximm8, sximm5;
input [2:0] writenum, anum, bnum;
input [1:0] ALUop, shift, vsel;
input [7:0] PC; 
input [15:0] mdata; 
output [15:0] datapath_out, creg_out; 
output [2:0] status_out; 
//n wires used for load, m used for multiplexers, a used for ALU, s used for shifter. 
wire [15:0] n1, n2, n3, m1, m2, a1, a2, s1, ain, bin, cout;
wire [2:0] status; 

// Speed up RAM by directly exposing the C register
assign creg_out = cout;
//instantiate pipeline registers
//A register, from lab 5, provides a direct path from the register file to C
//vDFFE #(16) A(clk, loada, ain, n2);
assign n2 = ain;
//B register, from lab 5, provides a path from the register file, through the shifter, to C
//vDFFE #(16) B(clk, loadb, bin, n3); 
assign n3 = bin;
//C register, from lab 5, synchronizes the output to the clock, and is used for writing back to the register file.
vDFFE #(16) C(clk, loadc, cout, datapath_out); 
//Status register, extended from lab 5. It is only updated by the CMP instruction
//Provides 3 bits:
// - Z (zero flag): Set if the two values being compared are equal, or if the result is zero.
// - V (overflow flag): Set if the subtraction operation of CMP overflows
// - N (negative flag): Set if the result of the subtraction operation of CMP is negative
vDFFE #(3) S(clk, loads, status, status_out);

//instantiate shifter
shifter multiplier(n3, shift, s1); 

//instantiate multiplexers
//4-way mux to write back to the register file
bMUX writeback(vsel, cout, {8'b0, PC}, sximm8, mdata, m1); 
//2-way mux to select either A or 16'b0 as input to the ALU
vMUX amux(asel, 16'b0 , n2, a1);
//2-way mux to select either B or a sign-extended 5-bit immediate as input to the ALU
vMUX bmux(bsel, sximm5 ,s1, a2); 

//instantiate register
regfile REGFILE(m1, writenum, write, anum, bnum, clk, ain, bin);

//instantiate ALU
ALU alu(a1, a2, ALUop, cout, status); 

endmodule 

module vDFFE(clk, en, in, out); //from Lab 5 slide set. Flip-flop with enable.
  parameter n = 1; // width
  input clk, en;
  input [n-1:0] in;
  output [n-1:0] out;
  reg [n-1:0] out;
  wire [n-1:0] next_out;
  // Out is only updated if enable is high
  assign next_out = en ? in : out;
  // Synchronizes out to the clock
  always @(posedge clk)
    out = next_out;
endmodule

module vMUX(select, inA, inB, out); // Two-way mux
  input[15:0] inA, inB;
  input select; 
  output[15:0] out; 
  reg[15:0] out;
  // selects inA if select is high, or inB otherwise.
  always @(*) begin
    case (select)
	  1'b1: out = inA;
	  1'b0: out = inB;
	  default: out = 16'b0;
	endcase
  end
endmodule

module bMUX(select, inA, inB, inC, inD, out); // Four-way mux
input [1:0] select; 
input [15:0] inA, inB, inC, inD; 
output reg [15:0] out;
// Selects an input depending on the binary signal select
 always@(*) begin
 case(select)
  2'b00 : out = inA;
  2'b01 : out = inB;
  2'b10 : out = inC; 
  2'b11 : out = inD;
endcase
end
endmodule 