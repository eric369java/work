module regfile(data_in, writenum, write, anum, bnum, clk, a_out, b_out);
  input [15:0] data_in;
  input [2:0] writenum, anum, bnum;
  input write, clk;
  output [15:0] a_out, b_out;
  wire [7:0] writeindex;
  wire [7:0] writeEnable;
  wire [15:0] R0;
  wire [15:0] R1;
  wire [15:0] R2;
  wire [15:0] R3;
  wire [15:0] R4;
  wire [15:0] R5;
  wire [15:0] R6;
  wire [15:0] R7;
  wire [127:0] readInputs;

  // Binary to one-hot decoder, part of the binary mux controlling which register to write to.
  decoder #(3,8) writeDec(writenum, writeindex);
  // Enables the register corresponding to the output from the binary to one-hot decoder
  assign writeEnable = writeindex & {8{write}};
  // Convenience bus used for input to the read mux
  assign readInputs = {R7, R6, R5, R4, R3, R2, R1, R0};
  // Flip-flop with enable controlling R0
  vDFFE #(16) R0DFF(clk, writeEnable[0], data_in, R0);
  // Flip-flop with enable controlling R1
  vDFFE #(16) R1DFF(clk, writeEnable[1], data_in, R1);
  // Flip-flop with enable controlling R2
  vDFFE #(16) R2DFF(clk, writeEnable[2], data_in, R2);
  // Flip-flop with enable controlling R3
  vDFFE #(16) R3DFF(clk, writeEnable[3], data_in, R3);
  // Flip-flop with enable controlling R4
  vDFFE #(16) R4DFF(clk, writeEnable[4], data_in, R4);
  // Flip-flop with enable controlling R5
  vDFFE #(16) R5DFF(clk, writeEnable[5], data_in, R5);
  // Flip-flop with enable controlling R6
  vDFFE #(16) R6DFF(clk, writeEnable[6], data_in, R6);
  // Flip-flop with enable controlling R7
  vDFFE #(16) R7DFF(clk, writeEnable[7], data_in, R7);
  // Mux used for reading data out of the register file  
  binMUX16 AMUX(anum, readInputs, a_out);
  binMUX16 BMUX(bnum, readInputs, b_out);
endmodule

module decoder(inbits, outbits); //from slide set 6. 
  parameter n = 1;
  parameter k = 2;
  input [n-1:0] inbits;
  output [k-1:0] outbits;
  reg [k-1:0] outbits;
  // All bits except bit inbits is set to 0
  always @(*) begin
    outbits = 0;
    outbits[inbits] = 1'b1;
  end
endmodule

module binMUX16(binindex, inputs, outputs); // 8-way 16-bit binary mux used for reading a register

  input [127:0] inputs;
  input [2:0] binindex;

  output [15:0] outputs;
  reg [15:0] outputs;
  // outputs is assigned to the binindex-th range of 16 bits in inputs
  always @(*) begin
    case (binindex)
      3'b000: outputs = inputs[15:0];
      3'b001: outputs = inputs[31:16];
      3'b010: outputs = inputs[47:32];
      3'b011: outputs = inputs[63:48];
      3'b100: outputs = inputs[79:64];
      3'b101: outputs = inputs[95:80];
      3'b110: outputs = inputs[111:96];
      3'b111: outputs = inputs[127:112];
	  default: outputs = 16'b0;
    endcase
  end
endmodule
