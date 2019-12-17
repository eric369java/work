module cpu(clk, reset, read_data, write_data, mem_cmd, mem_addr, N, V, Z, halt);
  input clk, reset;   
  input [15:0] read_data;
  output [15:0] write_data;
  output [1:0] mem_cmd;
  output [8:0] mem_addr; 
  output N, V, Z, halt;

  wire [15:0] IR, sximm5, sximm8, cout;
  wire [2:0] anum, bnum, wnum, writenum, regnum, ansel, bnsel, wnsel, opcode, cond;
  wire [8:0] PC; 
  wire [1:0] ALUop, op, shift, vsel;
  wire asel, bsel, loadc, loads, write, load_ir;
  wire load_pc, load_addr, reset_pc, addr_sel, trigger_branch; 
  wire [8:0] next_pc, data_out;  
	
  // Instruction register: Holds the current instruction
  vDFFE #(16) IRreg(clk, load_ir, read_data, IR);
  // Instruction decoder: decodes the current instruction, providing the inputs to the state machine as well as the inputs to the datapath.
  InDecoder ID(IR, ansel, bnsel, wnsel, ALUop, sximm5, sximm8, shift, anum, bnum, wnum, op, opcode, cond);
  // Datapath, which is controlled by the instruction decoder and the FSM.
  datapath DP(clk, anum, bnum, vsel, 
    shift, asel, bsel, ALUop, loadc, 
    loads, wnum, write, {Z, V, N}, write_data, cout, sximm5, sximm8, PC[7:0], read_data);
  //finite state machine controls fetching instructions and datapath. 
  CPUFSM FSM(clk, reset, opcode, op, ansel, bnsel, wnsel, asel, bsel, vsel, addr_sel, 
          loadc, loads, load_pc, load_addr, load_ir,
         reset_pc, mem_cmd, write, halt, trigger_branch);

   //MUX to choose either to reset program counter or increment program counter. 
  program_counter PCounter(reset_pc, PC, IR, write_data, Z, V, N, next_pc, trigger_branch);  
  //Program Counter Register.
  vDFFE #(9) PCReg(clk, load_pc, next_pc, PC); 
  //Data Address Register. 
  vDFFE #(9) AddressReg(clk, load_addr, cout[8:0], data_out); 
  //MUX to select between program register as input or the data address register as input. 
  assign mem_addr = addr_sel ? PC : data_out; 


endmodule
