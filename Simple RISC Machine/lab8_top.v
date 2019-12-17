module lab8_top(KEY,SW,LEDR,HEX0,HEX1,HEX2,HEX3,HEX4,HEX5, CLOCK_50);
input [3:0] KEY;
input [9:0] SW;
output [9:0] LEDR;
output [6:0] HEX0, HEX1, HEX2, HEX3, HEX4, HEX5;
input CLOCK_50;

wire[1:0] mem_cmd;
wire[8:0] mem_addr;
wire[15:0] read_data, write_data, dout; 
wire ovf, neg, zero, write, read; 

//write enables writing to memory
assign write = (mem_addr[8] == 1'b0) & (mem_cmd == 2'b10); 
//read enables reading from memory. 
assign read = (mem_addr[8] == 1'b0) & (mem_cmd == 2'b01); 

//instantiating a CPU
cpu CPU(.clk(CLOCK_50), .reset(~KEY[1]), .read_data(read_data), .write_data(write_data), .mem_cmd(mem_cmd),
	.mem_addr(mem_addr), .N(neg), .V(ovf), .Z(zero), .halt(LEDR[8])); 
//instantiating memory
RAM #(16, 8) MEM(.clk(CLOCK_50), .read_address(mem_addr[7:0]), .write_address(mem_addr[7:0]), .write(write),
	.din(write_data), .dout(dout)); 
//tri state buffer to control dout of the memory
tri_state_buffer #(16) DOUT(dout, read, read_data); 

//lighting the LED.  
led_interface LEDS(CLOCK_50, mem_cmd, mem_addr, write_data[7:0], LEDR[7:0]); 
//turning off leds that we aren't using. 
assign LEDR[9] = 1'b0;

//connecting to switches. 
switch_interface SWITCH(mem_cmd, mem_addr, SW[7:0], read_data); 

//turning off any outputs we aren't using. HEX5 displays status flags. 
assign HEX5[0] = ~ovf; //displays overflow
assign HEX5[6] = ~neg; //displays negative
assign HEX5[3] = ~zero; //displays cmp equals or zero. 
assign {HEX5[2:1],HEX5[5:4]} = 4'b1111; //turns off unneeded displays. 
assign HEX4 = 7'b1111_111; //turn off 
//first 4 hex displays datapath_out aka. write data. design transferred over from lab6 top. 
sseg H0(write_data[3:0],   HEX0);
sseg H1(write_data[7:4],   HEX1);
sseg H2(write_data[11:8],  HEX2);
sseg H3(write_data[15:12], HEX3);


endmodule 

//Citation: Read-Write Memory from Slide Set 7
module RAM(clk, read_address, write_address, write, din, dout); 
parameter data_width = 32;
parameter addr_width = 4; 
parameter filename = "lab8_demo.txt";

input clk;
input [addr_width-1:0] read_address, write_address; 
input write; 
input [data_width-1:0] din;
output [data_width-1:0] dout;
reg [data_width-1:0] dout; 

reg [data_width-1:0] mem [2**addr_width-1:0];

initial $readmemb(filename, mem); 

always @(posedge clk) begin
 if(write)
  mem[write_address] <= din;
 dout <= mem[read_address]; 
end
endmodule

module tri_state_buffer(in, en, out);
parameter n = 2;
input [n-1:0] in;
input en; 
output reg [n-1:0] out;
//if enable is on out equals in else out is high impedance. 
always @(*) begin
	if(en) out <= in;
	else out <= {n{1'bz}};  
end 
endmodule

module led_interface(clk, command, address, data, out);
input [1:0] command; 
input [8:0] address;
input [7:0] data; 
input clk;
output [7:0] out; 

wire enable; 
//check that mem_cmd is in read and mem_addr is h100.
assign enable = (address == 9'b100_000_000) & (command == 2'b10);
//store the led display. 
vDFFE #(8) LIGHT(clk, enable, data, out); 

endmodule 

module switch_interface(command, address, in, out);
input[1:0] command; 
input[8:0] address; 
input[7:0] in;
output[15:0] out; 

wire enable; 
wire [7:0] buffer1, buffer2; 

//check that mem_cmd is in write and mem_addr is in h140. 
assign enable = (command == 2'b01) & (address == 9'b101_000_000); 
//tri state buffer to allow switch input to connect to write_data
tri_state_buffer #(8) T1(8'b0, enable, buffer2); 
tri_state_buffer #(8)  T2(in, enable, buffer1);
assign out = {buffer2, buffer1}; 

endmodule

//segs from lab6 top. 
module sseg(in,segs);
  input [3:0] in;
  output reg [6:0] segs;

  always @(*) begin
    case (in)
      0: segs = 7'b100_0000;
      1: segs = 7'b111_1001;
      2: segs = 7'b010_0100;
      3: segs = 7'b011_0000;
      4: segs = 7'b001_1001;
      5: segs = 7'b001_0010;
      6: segs = 7'b000_0010;
      7: segs = 7'b111_1000;
      8: segs = 7'b000_0000;
      9: segs = 7'b001_0000;
      10: segs = 7'b000_1000;
      11: segs = 7'b000_0011;
      12: segs = 7'b100_0110;
      13: segs = 7'b010_0001;
      14: segs = 7'b000_0110;
      15: segs = 7'b000_1110;
    default: segs = 7'b111_1111;
    endcase
  end
endmodule

