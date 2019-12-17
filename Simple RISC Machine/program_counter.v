module program_counter(reset, pc, IR, in, Z, V, N, next_pc, trigger_branch);
	input [15:0] IR; //instruction register
	input [15:0] in; //write_data or datapath_out or Rd 
	input [8:0] pc; //PC
	input reset, Z, V, N; //status flags
	input trigger_branch; // Flag set by FSM to trigger the branching logic
	output reg [8:0] next_pc;  // Next value of PC

	wire LT = (N !== V); // Less than flag: N flag is not equal to V flag
	wire LE = ((N !== V) | Z); // Less than or equal flag: OR of LT and Z (equal)
    wire [8:0] IMM8 = {IR[7], IR[7:0]}; // Sign-extend the lowest 8 bits of IR to 9 bits

	always @(*) begin
		if(reset) next_pc = 9'b0; //If reset is high, ignore trigger_branch
		else if (trigger_branch) begin
			if(IR[15:13] == 3'b001) begin 
			// If the instruction is a branch-type instruction (B, BEQ, BNE, BLT, BLE)
				case(IR[10:8])
					3'b000 : next_pc = pc + IMM8; //B: Always branch: Set PC = PC + sximm8
					3'b001 : next_pc = Z ? pc + IMM8 : pc; //BEQ: Branch if Z is set
					3'b010 : next_pc = Z ? pc : pc + IMM8; //BNE: Branch if Z is not set
					3'b011 : next_pc = LT? pc + IMM8 : pc; //BLT: Branch if LT is set
					3'b100 : next_pc = LE? pc + IMM8 : pc; //BLE: Branch if LE is set
					default: next_pc = 9'bx; // Error state
				endcase 
			end 
			else if(IR[15:13] == 3'b010) begin 
			// If the instruction is a call-type instruction (BL, BX, BLX)
				casex(IR[12:11]) 
					2'b11 : next_pc = pc + IMM8; // BL: Set PC = PC + sximm8
					2'bx0 : next_pc = in[8:0];  //BX and BLX: Set PC = datapath_out[8:0]
					default: next_pc = 9'bx; // Error state
				endcase 
			end
			else next_pc = pc; // If trigger_branch is high but the instruction is not a branch, do nothing
		end 
		else next_pc = pc + 9'b000_000_001; // During fetch, update PC to PC + 1. 
	end 
endmodule