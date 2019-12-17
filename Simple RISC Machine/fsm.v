`define MREAD 2'b01//value defined by FSM
`define MWRITE 2'b10//value defined by FSM

module CPUFSM(clk, reset, opcode, op,
 ansel, bnsel, wnsel, asel, bsel, vsel, addr_sel,
 loadc, loads, load_pc, load_addr, load_ir,
 reset_pc, mem_cmd, write, halt, trigger_branch);
  input reset, clk;
  input [2:0] opcode;
  input [1:0] op;
  output reg [2:0] ansel, bnsel, wnsel;
  output reg [1:0] vsel, mem_cmd;
  output reg asel, bsel, addr_sel, loadc, loads, load_pc, load_addr, load_ir, reset_pc, write, halt, trigger_branch;
  wire [2:0] cycle, new_cycle;
  reg [2:0] next_cycle;

  // Synchronous reset which sets the state to either reset or the new state
  assign new_cycle = reset ? 3'b110 : next_cycle;
  // Flip-flop to sychronize the state with the clock
  vDFF #(3) STATE(clk, new_cycle, cycle);

  // Always block implementing the state machine. Selects the next state and controls the datapath.
  always @(*) begin
    // Initializes the state machine, preventing inferred latches
    {write, asel, bsel, vsel, ansel, bnsel, wnsel, addr_sel, loadc, loads, load_addr} = 0;
    {load_pc, load_ir, reset_pc, mem_cmd} = 0;
    next_cycle = 3'b111;
	halt = 1'b0;
	trigger_branch = 1'b0;
    // The full state is {opcode, op, cycle}, to simplify the operation of the state machine.
    casex ({opcode, op, cycle})
      8'b11010000:
        begin
        // MOV Rn, imm8. Writes imm8 to Rn, and updates the program counter
        // Transitions to the IF2 (fetch) state.
        write = 1'b1;
        vsel = 2'b10;
        wnsel = 3'b100;
        load_pc = 1'b1;
        addr_sel = 1'b1;
        next_cycle = 3'b101;
        mem_cmd = `MREAD;
        end
      8'b11000000:
        begin
        // MOV Rd, Rm, sh_op. Executes the full instruction, and updates the program counter
		// Transitions to the IF2 (fetch) state
        bnsel = 3'b001;
        vsel = 2'b00;
		asel = 1'b1;
		loadc = 1'b1;
        wnsel = 3'b010;
        write = 1'b1;
		load_pc = 1'b1;
        addr_sel = 1'b1;
        next_cycle = 3'b101;
        mem_cmd = `MREAD;
        end
      8'b101XX000:
        begin
        // The only cycle of all ALU instructions. Executes the instruction as specified in the lab 6 PDF
		// Also updates the program counter
		// Transitions to the IF2 (fetch) state
        bnsel = 3'b001;
        ansel = 3'b100;
		if (op == 2'b01) loads = 1'b1;
		else write = 1'b1;
		if (op == 2'b11) asel = 1'b1;
		wnsel = 3'b010;
		load_pc = 1'b1;
        addr_sel = 1'b1;
        next_cycle = 3'b101;
        mem_cmd = `MREAD;
        end
      8'b01100000:
        begin
		// LDR First cycle: Loads Rn + sximm5 into addr
        // Transitions to the second cycle of LDR
        ansel = 3'b100;
        next_cycle = 3'b001;
        load_addr = 1'b1;
		bsel = 1'b1;
        end
      8'b10000000:
        begin
        // STR First cycle: Loads Rn + sximm5 into addr
		// Transitions to the second cycle of STR
        ansel = 3'b100;
        next_cycle = 3'b001;
		bsel = 1'b1;
		load_addr = 1'b1;
        end
      8'b01100001:
        begin
        // LDR second cycle: Send read command to memory
		// Transitions to the third cycle of LDR
        mem_cmd = `MREAD;
        next_cycle = 3'b010;
        end
      8'b01100010:
        begin
        // LDR third cycle: Writes the data from ram into the register file
		// Transitions to the IF1 (fetch) cycle
        mem_cmd = `MREAD;
        vsel = 2'b11;
        wnsel = 3'b010;
        write = 1'b1;
        next_cycle = 3'b100;
        end
      8'b10000001:
        begin
        // STR second cycle: Read Rd into C
		// Transitions to the third cycle of STR
        bnsel = 3'b010;
		asel = 1'b1;
		loadc = 1'b1;
        next_cycle = 3'b010;
        end
      8'b10000010:
        begin
        // STR third cycle: Write C into the memory
		// Transitions to the IF1 state
        mem_cmd = `MWRITE;
        next_cycle = 3'b100;
        end
      8'bxxxxx100:
        begin
        // The IF1 state. Loads the program counter.
        load_pc = 1'b1;
        addr_sel = 1'b1;
        next_cycle = 3'b101;
        mem_cmd = `MREAD;
        end
      8'bxxxxx101:
        begin
        // The IF2 state. Reads the instruction from memory
        load_ir = 1'b1;
        next_cycle = 3'b000;
        addr_sel = 1'b1;
        mem_cmd = `MREAD;
        end
      8'bxxxxx110:
        begin
        // The Reset state. Resets the PC
        reset_pc = 1'b1;
        load_pc = 1'b1;
        next_cycle = 3'b100;
        end
	  8'b00100000:
	    begin
		// Branch: Triggers the branch function of the program counter
		// This state catches B, BEQ, BNE, BLT, and BLE
		// The logic to actually perform the branch is in the program counter
		// Transitions to the IF1 (fetch) state
		trigger_branch = 1'b1;
		load_pc = 1'b1;
		next_cycle = 3'b100;
		end
      8'b01011000:
	    begin
		// Direct Call (BL) first cycle: Sets R7 to PC
		// Transitions to the second cycle of BL
		vsel = 2'b01;
		write = 1'b1;
		wnsel = 3'b100;
		next_cycle = 3'b001;
		end
      8'b01011001:
	    begin
		// Direct Call (BL) second cycle: Triggers the branch function of the program counter
		// The functionality to set PC = PC + 1 + sximm8 is in the program counter
		// Transitions to the IF1 (fetch) state
		trigger_branch = 1'b1;
		load_pc = 1'b1;
		next_cycle = 3'b100;
		end
      8'b01000000:
	    begin
		// Return (BX) first cycle: Loads R7 into datapath_out
		// Transitions to the second cycle of BX
		asel = 1'b1;
		bnsel = 3'b010;
		loadc = 1'b1;
		next_cycle = 3'b001;
		end
      8'b01000001:
	    begin
		// Return (BX) second cycle: Loads datapath_out into PC
		// The functionality to set PC is in the program counter
		// Transitions to the IF1 (fetch) state
		trigger_branch = 1'b1;
		load_pc = 1'b1;
		next_cycle = 3'b100;
		end
      8'b01010000:
	    begin
		// Indirect Call (BLX) first cycle: Writes PC into R7
		// Transitions to the second cycle of BLX
		bnsel = 3'b010;
		ansel = 3'b010;
		loadc = 1'b1;
		vsel = 2'b01;
		write = 1'b1;
		wnsel = 3'b100;
		next_cycle = 3'b001;
		end
      8'b01010001:
	    begin
		// Indirect Call (BLX) second cycle: Updates PC
		// Transitions to the IF1 (fetch) cycle
		trigger_branch = 1'b1;
		load_pc = 1'b1;
		next_cycle = 3'b100;
		end
      8'bxxxxx111:
	    begin
        next_cycle = 3'b111; // Halt state: Transitions to itself
		halt = 1'b1;
		end
      8'b11100xxx:
        begin
        // Halt instruction: Transitions to the halt state
        next_cycle = 3'b111;
        end
      default:
        begin
        // An error state. This should never be reached in practice. Halts the CPU
        next_cycle = 3'b111;
        end
    endcase
  end
endmodule
module vDFF(clk,D,Q);
  parameter n=1;
  input clk;
  input [n-1:0] D;
  output [n-1:0] Q;
  reg [n-1:0] Q;
  always @(posedge clk)
    Q <= D;
endmodule