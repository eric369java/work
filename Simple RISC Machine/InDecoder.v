module InDecoder(in, ansel, bnsel, wnsel, ALUop, sximm5, sximm8, shift, anum, bnum, wnum, op, opcode, cond); 
input [15:0] in;
input [2:0] ansel, bnsel, wnsel; 
output [1:0] ALUop, shift, op;
output [15:0] sximm5, sximm8;
output [2:0] anum, bnum, wnum, opcode, cond; 

wire [1:0] ALUop, shift, op;
wire [15:0] sximm5, sximm8;
wire [2:0] regnum, opcode;

// Sign-extend the 5 least significant bits of the input instruction, and store the result in sximm5
signExtend5 s5(in[4:0], sximm5); 
// Sign-extend the 8 least significant bits of the input instruction, and store the result in sximm8
signExtend8 s8(in[7:0], sximm8);
// Three-way mux to select a register to read or write
MUX3 amux(in[2:0], in[7:5], in[10:8], ansel, anum);
MUX3 bmux(in[2:0], in[7:5], in[10:8], bnsel, bnum);
MUX3 wmux(in[2:0], in[7:5], in[10:8], wnsel, wnum);

// Assign ALUop to the correct bits in the input instruction, according to the simple RISC machine instruction specification
assign ALUop = in[12:11];
// Assign shift to the correct bits in the input instruction, according to the simple RISC machine instruction specification
assign shift = in[4:3];
// Assign the opcode to the 3 most significant bits of the input instruction, according to the simple RISC machine instruction specification
assign opcode = in[15:13];
// Assign the operation to execute, according to the simple RISC machine instruction specification
assign op = in[12:11]; 
// Assign the condition for branching
assign cond = in[10:8];
endmodule

//Sign-extend 5-bit num to 16 bits
module signExtend5(num,snum);
input [4:0] num; 
output reg [15:0] snum; 
  
// Sign-extend num, or assign 16 xs to snum in simulation if num is invalid
always @(*) begin
 if(num[4] == 1'b1) 
   snum = {11'b1111_1111_111,num[4:0]};
 else if(num[4] == 1'b0) 
   snum = {11'b0,num[4:0]};
 else snum = 16'bxxxx_xxxx_xxxx_xxxx; 
end
endmodule

//Sign-extend 8-bit num to 16 bits
module signExtend8(num,snum);   
input [7:0] num;
output reg [15:0] snum; 

// Sign-extend num, or assign 16 xs to snum in simulation if num is invalid
always @(*) begin
 if(num[7] == 1'b1) snum = {8'b1111_1111,num[7:0]};
 else if(num[7] == 1'b0) snum = {8'b0,num[7:0]};
 else snum = 16'bxxxx_xxxx_xxxx_xxxx; 
end
endmodule

// 3-way one-hot mux
module MUX3(a,b,c,sel,out); 
input [2:0] a,b,c,sel;
output reg [2:0] out;

// selects between a, b, or c depending on the value of sel
always @(*) begin
 case(sel)
  3'b001 : out = a; // Rm
  3'b010 : out = b; // Rd
  3'b100 : out = c; // Rn
 default out = 3'bxxx; 
 endcase
end
endmodule 
