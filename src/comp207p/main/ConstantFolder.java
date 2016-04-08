package comp207p.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.bcel.classfile.*;

import org.apache.bcel.generic.*;

import org.apache.bcel.util.InstructionFinder;



public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimised = null;

	InstructionList originalilist = null;
	InstructionList newilist = null;

    Constant[] originalcp = null;
    Constant[] newcp = null;

    ConstantPoolGen originalcpgen = null;


	public ConstantFolder(String classFilePath)
	{
		try{
			this.parser = new ClassParser(classFilePath);
			System.out.println(classFilePath);
			this.original = this.parser.parse();
			this.gen = new ClassGen(this.original);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	private Number[] getLatestValues(InstructionHandle handle) {
		Number[] nums = new Number[2];
		Number temp;
		temp = handleOperations(handle.getPrev());
        if(temp == null) {
            return null;
        } else {
            nums[0] = temp;
        }
		temp = handleOperations(handle.getPrev());
        if(temp == null) {
            return null;
        } else {
            nums[1] = temp;
        }
        return nums;
 	}

	private boolean handleArithmetic(InstructionHandle handle) {
        Number value = handleOperations(handle);
        int constantIndex = 0;
        System.out.println("ARITHMETIC : " + value);
        if(value != null){
            if (value instanceof Integer) {
                constantIndex = originalcpgen.addInteger((int) value);
                this.newilist.insert(handle, new LDC(constantIndex));
                this.newilist.setPositions();
            } else if (value instanceof Float) {
                constantIndex = originalcpgen.addFloat((float) value);
                this.newilist.insert(handle, new LDC(constantIndex));
                this.newilist.setPositions();
            } else if (value instanceof Double) {
                constantIndex = originalcpgen.addDouble((double) value);
                this.newilist.insert(handle, new LDC2_W(constantIndex));
                this.newilist.setPositions();
            } else if (value instanceof Long) {
                constantIndex = originalcpgen.addLong((long) value);
                this.newilist.insert(handle, new LDC2_W(constantIndex));
                this.newilist.setPositions();
            }
        }
        if(removeInstruction(handle)) return true;
		return false;
	}

    private boolean handleStore(InstructionHandle handle) {
        boolean success = propogate(handle);
        if(success){
            removeInstruction(handle);
            return true;
        } else return false;
    }

	// Get values from operations that generate values

	// ArithmeticInstruction : 
	// DADD, DDIV, DMUL, DNEG, DREM, DSUB, FADD, FDIV, FMUL, FNEG, FREM, FSUB, IADD, IAND, IDIV, IMUL, INEG, IOR, IREM, ISHL, ISHR, ISUB, IUSHR, IXOR, LADD, LAND, LDIV, LMUL, LNEG, LOR, LREM, LSHL, LSHR, LSUB, LUSHR, LXOR
	// Comparisons :
	// DCMPG, DCMPL, FCMPG, FCMPL, LCMP
	// Constants And Pushes : 
	// DCONST, FCONST, ICONST, LCONST, BIPUSH, SIPUSH
	// ConversionInstruction :
	// D2F, D2I, D2L, F2D, F2I, F2L, I2B, I2C, I2D, I2F, I2L, I2S, L2D, L2F, L2I

	private Number handleOperations(InstructionHandle handle) {
		
		// Cycles through instructions until a stack changing operation is found
		InstructionHandle prevHandle = handle;

        while(prevHandle != null && (isInstruction(prevHandle) == 0 )){
            prevHandle = prevHandle.getPrev();
        }

		if(prevHandle.getInstruction() instanceof DADD) {
			Number[] nums = getLatestValues( prevHandle);
			if (nums == null) return null;
            return ((double) nums[0] + (double) nums[1] );
		} else if(prevHandle.getInstruction() instanceof DDIV) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((double) nums[1] / (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DMUL) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((double) nums[0] * (double) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof DNEG) {
            Number nums = handleOperations( prevHandle);
            if(nums == null) return null;
            if(removeInstruction(prevHandle)) return (0 - (double) nums );
            else return null;
		} else if(prevHandle.getInstruction() instanceof DREM) {
            Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((double) nums[1] % (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DSUB) {
			Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((double) nums[1] - (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FADD) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((float) nums[0] + (float) nums[1] );
		} else if(prevHandle.getInstruction() instanceof FDIV) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((float) nums[1] / (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FMUL) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((float) nums[0] * (float) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof FNEG) {
			Number nums = handleOperations( prevHandle);
            if(nums == null) return null;
            if(removeInstruction(prevHandle)) return (0 - (float) nums );
            else return null;
		} else if(prevHandle.getInstruction() instanceof FREM) {
			Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((float) nums[1] % (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FSUB) {
			Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((float) nums[1] - (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IADD) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] + (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IAND) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] & (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IDIV) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] / (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IMUL) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[0] * (int) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof INEG) {
			Number nums = handleOperations( prevHandle);
            if (nums == null) return null;
            if(removeInstruction(prevHandle)) return (0 - (int) nums );
            else return null;
		} else if(prevHandle.getInstruction() instanceof IOR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] | (int) nums[0] ); 
		} else if(prevHandle.getInstruction() instanceof IREM) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] % (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISHL) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] << (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISHR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] >> (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISUB) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] - (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IUSHR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] >>> (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IXOR) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] ^ (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LADD) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] + (long) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LAND) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] & (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LDIV) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] / (long) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LMUL) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] * (long) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof LNEG) {
			Number nums = handleOperations( prevHandle);
            if (nums == null) return null;
            if(removeInstruction(prevHandle)) return (0 - (long) nums );
            else return null;
		} else if(prevHandle.getInstruction() instanceof LOR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] | (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LREM) {
			Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((long) nums[1] % (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSHL) {
			Number[] nums = getLatestValues( prevHandle);
            if(nums == null) return null;
            return ((long) nums[1] << (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSHR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] >> (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSUB) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] - (long) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LUSHR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] >>> (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LXOR) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] ^ (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DCMPG) {
			Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            double temp = (double) nums[1] - (double) nums[0];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
		} else if(prevHandle.getInstruction() instanceof DCMPL) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            double temp = (double) nums[0] - (double) nums[1];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
			
		} else if(prevHandle.getInstruction() instanceof FCMPG) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            float temp = (float) nums[1] - (float) nums[0];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
			
		} else if(prevHandle.getInstruction() instanceof FCMPL) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            float temp = (float) nums[0] - (float) nums[1];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
			
		} else if(prevHandle.getInstruction() instanceof LCMP) {
            Number[] nums = getLatestValues( prevHandle);
            if (nums == null) return null;
            long temp = (long) nums[1] - (long) nums[0];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
		} else if(prevHandle.getInstruction() instanceof DCONST) {
            Number value = ((DCONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
		} else if(prevHandle.getInstruction() instanceof FCONST) {
            Number value = ((FCONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof ICONST) {
            Number value = ((ICONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof LCONST) {
            Number value = ((LCONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof BIPUSH) {
            Number value = ((BIPUSH) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof SIPUSH) {
            Number value = ((SIPUSH) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof D2F) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (float) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof D2I) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (int) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof D2L) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (long) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof F2D) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (double) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof F2I) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (int) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof F2L) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (long) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof I2B) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (byte) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2D) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (double) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2F) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (float) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2L) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (long) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2S) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (short) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof L2D) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (double) ( (long) num);
        } else if(prevHandle.getInstruction() instanceof L2F) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (float) ( (long) num);
        } else if(prevHandle.getInstruction() instanceof L2I) {
            Number num = handleOperations( prevHandle);
            if(num == null) return null;
            return (int) ( (long) num);
        } else if(prevHandle.getInstruction() instanceof LDC) {
            LDC ldc = (LDC) prevHandle.getInstruction();
            Number value = (Number) ldc.getValue(originalcpgen);
            Instruction i = prevHandle.getInstruction();
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof LDC_W) {
            LDC_W ldc_w = (LDC_W) prevHandle.getInstruction();
            Number value = (Number) ldc_w.getValue(originalcpgen);
            if(removeInstruction(prevHandle)) return value;
            else return null;
        } else if(prevHandle.getInstruction() instanceof LDC2_W) {
            LDC2_W ldc2_w = (LDC2_W) prevHandle.getInstruction();
            Number value = (Number) ldc2_w.getValue(originalcpgen);
            if(removeInstruction(prevHandle)) return value;
            else return null;
        }
        return null;
	}

	private boolean propogate(InstructionHandle handle) {
        System.out.print("HANDLING STORE: ");
        System.out.println(handle.getInstruction());
        Number value = handleOperations(handle.getPrev());
        System.out.println("VALUE : " + value);

        int storeIndex = 0, constantIndex = 0;
        InstructionHandle nextHandle = handle.getNext();

        if (handle.getInstruction() instanceof ISTORE && value != null) {
            int result = (int) value;
            storeIndex = ((ISTORE) handle.getInstruction()).getIndex();
            if (result < -32768 || result > 32767) constantIndex = originalcpgen.addInteger(result);
            while (nextHandle != null && !(nextHandle.getInstruction() instanceof ISTORE && ((ISTORE) nextHandle.getInstruction()).getIndex() == storeIndex && nextHandle.getInstruction().getOpcode() == handle.getInstruction().getOpcode())){
                if(nextHandle.getInstruction() instanceof ILOAD && ((ILOAD) nextHandle.getInstruction()).getIndex() == storeIndex) {
                    if (result < -32767 || result > 32768) {
                        this.newilist.insert(nextHandle, new LDC(constantIndex));
                    }
                    else if (result < -128 || result > 127) {
                        this.newilist.insert(nextHandle, new SIPUSH((short) result));
                    }
                    else {
                        this.newilist.insert(nextHandle, new BIPUSH((byte) result));
                    }
                    this.newilist.setPositions();
                    try {
                        boolean success = removeInstruction(nextHandle);
                        nextHandle = nextHandle.getNext();
                    } catch (Exception e) {}
                } else {
                    nextHandle = nextHandle.getNext();
                }
            }
            return true;
        } else if (handle.getInstruction() instanceof DSTORE && value != null) {
            double result = (double) value;
            storeIndex = ((DSTORE) handle.getInstruction()).getIndex();
            constantIndex = originalcpgen.addDouble(result);
            while (nextHandle != null && !(nextHandle.getInstruction() instanceof DSTORE && ((DSTORE) nextHandle.getInstruction()).getIndex() == storeIndex && nextHandle.getInstruction().getOpcode() == handle.getInstruction().getOpcode())){
                if(nextHandle.getInstruction() instanceof DLOAD && ((DLOAD) nextHandle.getInstruction()).getIndex() == storeIndex) {
                    this.newilist.insert(nextHandle, new LDC2_W(constantIndex));
                    this.newilist.setPositions();
                    try {
                        boolean success = removeInstruction(nextHandle);
                        nextHandle = nextHandle.getNext();
                    } catch (Exception e) {}
                } else {
                    nextHandle = nextHandle.getNext();
                }
            }
            return true;
        } else if (handle.getInstruction() instanceof FSTORE && value != null) {
            float result = (float) value;
            storeIndex = ((FSTORE) handle.getInstruction()).getIndex();
            constantIndex = originalcpgen.addFloat(result);
            while (nextHandle != null && !(nextHandle.getInstruction() instanceof FSTORE && ((FSTORE) nextHandle.getInstruction()).getIndex() == storeIndex && nextHandle.getInstruction().getOpcode() == handle.getInstruction().getOpcode())){
                if(nextHandle.getInstruction() instanceof FLOAD && ((FLOAD) nextHandle.getInstruction()).getIndex() == storeIndex) {
                    this.newilist.insert(nextHandle, new LDC(constantIndex));
                    this.newilist.setPositions();
                    try {
                        boolean success = removeInstruction(nextHandle);
                        nextHandle = nextHandle.getNext();
                    } catch (Exception e) {}
                } else {
                    nextHandle = nextHandle.getNext();
                }
            }
            return true;
        } else if (handle.getInstruction() instanceof LSTORE && value != null) {
            long result = (long) value;
            storeIndex = ((LSTORE) handle.getInstruction()).getIndex();
            constantIndex = originalcpgen.addLong(result);
            while (nextHandle != null && !(nextHandle.getInstruction() instanceof LSTORE && ((LSTORE) nextHandle.getInstruction()).getIndex() == storeIndex && nextHandle.getInstruction().getOpcode() == handle.getInstruction().getOpcode())){
                if(nextHandle.getInstruction() instanceof LLOAD && ((LLOAD) nextHandle.getInstruction()).getIndex() == storeIndex) {
                    this.newilist.insert(nextHandle, new LDC2_W(constantIndex));
                    this.newilist.setPositions();
                    try {
                        boolean success = removeInstruction(nextHandle);
                        nextHandle = nextHandle.getNext();
                    } catch (Exception e) {}
                } else {
                    nextHandle = nextHandle.getNext();
                }
            }
            return true;
        } else {
            return false;
        }
	}

    private boolean handleOther(InstructionHandle handle) {
        if(handle.getInstruction() instanceof NOP) {
            if(removeInstruction(handle)) return true;
            else return false;
        } else if (handle.getInstruction() instanceof IINC) {
            int nextincrement = ((IINC) handle.getInstruction()).getIncrement();
            int iincIndex = ((IINC) handle.getInstruction()).getIndex();
            this.newilist.insert(handle, new BIPUSH((byte) nextincrement));
            this.newilist.insert(handle, new ILOAD(iincIndex));
            this.newilist.insert(handle, new IADD());
            this.newilist.insert(handle, new ISTORE(iincIndex));
            if(removeInstruction(handle)) return true;
            else return false;
        } else {
            return false;
        }
    }

	private int isInstruction(InstructionHandle handle) {
		if(handle.getInstruction() instanceof ArithmeticInstruction) {
			return 1;
		} else if (handle.getInstruction() instanceof StoreInstruction) {
			return 2;
		} else if (handle.getInstruction() instanceof LoadInstruction){
            return 3;
        } else if (handle.getInstruction() instanceof IINC) {
            return 4;
        } else if (handle.getInstruction() instanceof StackInstruction) {
			return 5;
		} else if (handle.getInstruction() instanceof DCONST || handle.getInstruction() instanceof FCONST
			|| handle.getInstruction() instanceof ICONST || handle.getInstruction() instanceof LCONST ) {
			return 6;
		} else if (handle.getInstruction() instanceof BIPUSH || handle.getInstruction() instanceof SIPUSH) {
			return 7;
		} else if (handle.getInstruction() instanceof DCMPG || handle.getInstruction() instanceof DCMPL
			|| handle.getInstruction() instanceof FCMPG || handle.getInstruction() instanceof FCMPL
			|| handle.getInstruction() instanceof LCMP) {
			return 8;
		} else if (handle.getInstruction() instanceof LDC || handle.getInstruction() instanceof LDC_W || handle.getInstruction() instanceof LDC2_W) {
            return 9;
        } else if (handle.getInstruction() instanceof NOP ) {
            return 11;
        } else {
			return 0;
		}
	}


    private boolean removeInstruction(InstructionHandle handle) {
        System.out.println("REMOVING INSTRUCTION : ");
        System.out.println(handle.getInstruction());
        if(handle.getPrev() != null) {
            this.newilist.redirectBranches(handle,handle.getPrev());            
        }
        try {
            this.newilist.delete(handle);
        } catch (Exception e) {}
        return true;
    }

	private Method optimiseInstructions(ClassGen cgen, ConstantPoolGen cpgen, Method method) {

		Code m = method.getCode();
		InstructionList ilist = new InstructionList(m.getCode());
        this.originalilist = ilist;
        this.newilist = ilist;

        MethodGen mgen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), this.newilist,
            cpgen);

        boolean success;
		for (InstructionHandle handle : this.newilist.getInstructionHandles()) {
            System.out.println("OPERATION : " + handle.getInstruction());

			int type = isInstruction(handle);
			switch(type) {
				case 1:
				    success = handleArithmetic(handle);
				break;
				case 2:
				    success = handleStore(handle);
				break;
                case 4:
                    success = handleOther(handle);
                break;
                case 11:
                    success = handleOther(handle);
				default:
				break;
			}
		}

		this.newilist.setPositions();

        mgen.setInstructionList(newilist);

		mgen.setMaxStack();
		mgen.setMaxLocals();

		Method newMethod = mgen.getMethod();
        return newMethod;
	}

	public void optimise()
	{
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();

        System.out.println("ORIGINAL :: ");
        printClass(cgen);

        this.originalcpgen = cpgen;

		ConstantPool cp = cpgen.getConstantPool();
		Constant[] constants = cp.getConstantPool();

        this.originalcp = constants;
        this.newcp = constants;

		Method[] methods = cgen.getMethods();

        for(int i = 0; i < methods.length; i++) {
            methods[i] = optimiseInstructions(cgen,cpgen,methods[i]);
        }

        gen.setConstantPool(originalcpgen);
        gen.setMethods(methods);
        gen.setMajor(50);
		this.optimised = gen.getJavaClass();

        // System.out.println();
        // System.out.println("NEW :: ");
        // printClass(gen);


	}

    private void printClass(ClassGen cgen) {
        ConstantPoolGen cpgen = cgen.getConstantPool();
        ConstantPool cp = cpgen.getConstantPool();
        Constant[] constants = cp.getConstantPool();

        Method[] methods = cgen.getMethods();

        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("Printing out constants!");
        System.out.println("+++++++++++++++++++++++++++++++++++");

        for(Constant c : constants) {
            if(c != null && !(c instanceof ConstantUtf8)) System.out.println(c);
        }

        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("Printing out methods!");
        System.out.println("+++++++++++++++++++++++++++++++++++");

        for (Method m : methods) {
            Code m2 = m.getCode();
            InstructionList ilist = new InstructionList(m2.getCode());
            for (InstructionHandle h : ilist.getInstructionHandles()) {
                System.out.println(h.getInstruction());
            }

        }

        System.out.println("+++++++++++++++++++++++++++++++++++");

    }

	
	public void write(String optimisedFilePath)
	{
		this.optimise();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimised.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}