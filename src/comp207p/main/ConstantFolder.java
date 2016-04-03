package comp207p.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Method;

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

	private Number[] getLatestValues(InstructionList ilist, InstructionHandle handle) {
		Number[] nums = new Number[2];
		Number temp;
		temp = handleOperations(ilist,handle);
        if(temp == null) {
            return null;
        } else {
            nums[0] = temp;
        }
		temp = handleOperations(ilist,handle);
        if(temp == null) {
            return null;
        } else {
            nums[1] = temp;
        }
		return nums; 
	}

	private InstructionList handleArithmetic(InstructionList ilist, InstructionHandle handle) {

		return null;
	}

    private InstructionList handleOther(InstructionList ilist, InstructionHandle handle){
        return null;
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

	private Number handleOperations(InstructionList ilist, InstructionHandle handle) {
		
		// Cycles through instructions until a stack changing operation is found
		InstructionHandle prevHandle = handle;

		while(isInstruction(prevHandle) == 0 || prevHandle != null) {
			prevHandle = prevHandle.getPrev();
		}

		if(prevHandle.getInstruction() instanceof DADD) {
			Number[] nums = getLatestValues(ilist, prevHandle);
			if (nums == null) return null;
            return ((double) nums[0] + (double) nums[1] );
		} else if(prevHandle.getInstruction() instanceof DDIV) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[1] / (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DMUL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[0] * (double) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof DNEG) {
            Number nums = handleOperations(ilist, prevHandle);
            if(nums == null) return null;
            return (0 - (double) nums );
		} else if(prevHandle.getInstruction() instanceof DREM) {
            Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((double) nums[1] % (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DSUB) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((double) nums[1] - (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FADD) {
            Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((float) nums[0] + (float) nums[1] );
		} else if(prevHandle.getInstruction() instanceof FDIV) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((float) nums[1] / (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FMUL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((float) nums[0] * (float) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof FNEG) {
			Number nums = handleOperations(ilist, prevHandle);
            if(nums == null) return null;
            return (0 - (float) nums );
		} else if(prevHandle.getInstruction() instanceof FREM) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((float) nums[1] % (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof FSUB) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((float) nums[1] - (float) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IADD) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] + (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IAND) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] & (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IDIV) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] / (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IMUL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[0] * (double) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof INEG) {
			Number nums = handleOperations(ilist, prevHandle);
            if (nums == null) return null;
            return (0 - (double) nums );
		} else if(prevHandle.getInstruction() instanceof IOR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] | (int) nums[0] ); 
		} else if(prevHandle.getInstruction() instanceof IREM) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[1] % (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISHL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] << (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISHR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] >> (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof ISUB) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[1] - (double) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IUSHR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] >>> (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof IXOR) {
            Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((int) nums[1] ^ (int) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LADD) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] + (long) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LAND) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] & (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LDIV) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] / (long) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LMUL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[0] * (long) nums[1] ); 
		} else if(prevHandle.getInstruction() instanceof LNEG) {
			Number nums = handleOperations(ilist, prevHandle);
            if (nums == null) return null;
            return (0 - (long) nums );
		} else if(prevHandle.getInstruction() instanceof LOR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] | (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LREM) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((long) nums[1] % (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSHL) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if(nums == null) return null;
            return ((long) nums[1] << (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSHR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] >> (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LSUB) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((double) nums[0] - (double) nums[1] );
		} else if(prevHandle.getInstruction() instanceof LUSHR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] >>> (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof LXOR) {
			Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            return ((long) nums[1] ^ (long) nums[0] );
		} else if(prevHandle.getInstruction() instanceof DCMPG) {
			Number[] nums = getLatestValues(ilist, prevHandle);
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
            Number[] nums = getLatestValues(ilist, prevHandle);
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
            Number[] nums = getLatestValues(ilist, prevHandle);
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
            Number[] nums = getLatestValues(ilist, prevHandle);
            if (nums == null) return null;
            double temp = (float) nums[0] - (float) nums[1];
            if (temp > 0 ) {
                return 1;
            } else if (temp < 0) {
                return -1;
            } else {
                return 0;
            }
			
		} else if(prevHandle.getInstruction() instanceof LCMP) {
            Number[] nums = getLatestValues(ilist, prevHandle);
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
            return value;
		} else if(prevHandle.getInstruction() instanceof FCONST) {
            Number value = ((FCONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            return value;
        } else if(prevHandle.getInstruction() instanceof ICONST) {
            Number value = ((ICONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            return value;
        } else if(prevHandle.getInstruction() instanceof LCONST) {
            Number value = ((LCONST) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            return value;
        } else if(prevHandle.getInstruction() instanceof BIPUSH) {
            Number value = ((BIPUSH) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            return value;
        } else if(prevHandle.getInstruction() instanceof SIPUSH) {
            Number value = ((SIPUSH) prevHandle.getInstruction()).getValue();
            if(value == null) return null;
            return value;
        } else if(prevHandle.getInstruction() instanceof D2F) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (float) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof D2I) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (int) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof D2L) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (long) ( (double) num);
        } else if(prevHandle.getInstruction() instanceof F2D) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (double) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof F2I) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (int) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof F2L) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (long) ( (float) num);
        } else if(prevHandle.getInstruction() instanceof I2B) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (byte) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2D) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (double) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2F) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (float) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2L) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (long) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof I2S) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (short) ( (int) num);
        } else if(prevHandle.getInstruction() instanceof L2D) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (double) ( (long) num);
        } else if(prevHandle.getInstruction() instanceof L2F) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (float) ( (long) num);
        } else if(prevHandle.getInstruction() instanceof L2I) {
            Number num = handleOperations(ilist, prevHandle);
            if(num == null) return null;
            return (int) ( (long) num);
        }
        return null;
	}

	private InstructionList handleStore(InstructionList ilist, InstructionHandle handle) {
        return null;
	}

	private int isInstruction(InstructionHandle handle) {
		if(handle.getInstruction() instanceof ArithmeticInstruction) {
			return 1;
		} else if (handle.getInstruction() instanceof LocalVariableInstruction) {
			return 2;
		} else if (handle.getInstruction() instanceof StackInstruction) {
			return 3;
		} else if (handle.getInstruction() instanceof DCONST || handle.getInstruction() instanceof FCONST
			|| handle.getInstruction() instanceof ICONST || handle.getInstruction() instanceof LCONST ) {
			return 4;
		} else if (handle.getInstruction() instanceof BIPUSH || handle.getInstruction() instanceof SIPUSH) {
			return 5;
		} else if (handle.getInstruction() instanceof DCMPG || handle.getInstruction() instanceof DCMPL
			|| handle.getInstruction() instanceof FCMPG || handle.getInstruction() instanceof FCMPL
			|| handle.getInstruction() instanceof LCMP) {
			return 6;
		} else {
			return 0;
		}
	}

    // Removes constants related to the instruction that you need to remove
    private ConstantPool removeConstant(InstructionList ilist, ConstantPool cp, InstructionHandle handle) {
        return cp;
    }

    private InstructionList removeInstruction(InstructionList ilist, InstructionHandle handle) {
        return ilist;
    }

	private void optimiseInstructions(ClassGen cgen, ConstantPoolGen cpgen, Method method) {

		Code m = method.getCode();
		InstructionList ilist = new InstructionList(m.getCode());
		MethodGen mgen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), ilist,
			cpgen);
		for (InstructionHandle handle : ilist.getInstructionHandles()) {
			// int type = isInstruction(ilist, handle);
			// switch(type) {
			// 	case 1:
			// 	handleArithmetic(ilist,handle);
			// 	break;
			// 	case 3:
			// 	handleStore(ilist,handle);
			// 	break;
			// 	default:
   //              handleOther(ilist,handle);
			// 	break;
			// }
			System.out.println(handle.getInstruction());

		}

		ilist.setPositions();
		mgen.setMaxStack();
		mgen.setMaxLocals();

		Method newMethod = mgen.getMethod();
		InstructionList newIList = new InstructionList(newMethod.getCode().getCode());
		cgen.replaceMethod(method, newMethod);

	}

	public void optimise()
	{
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();

		ConstantPool cp = cpgen.getConstantPool();
		Constant[] constants = cp.getConstantPool();

		Method[] methods = cgen.getMethods();

		System.out.println("+++++++++++++++++++++++++++++++++++");
		System.out.println("Printing out constants!");
		System.out.println("+++++++++++++++++++++++++++++++++++");

        for(Constant c : constants) {
            if(c != null) System.out.println(c);
        }

        System.out.println("+++++++++++++++++++++++++++++++++++");
        System.out.println("Printing out methods!");
        System.out.println("+++++++++++++++++++++++++++++++++++");

		for(Method m : methods) {
			optimiseInstructions(cgen, cpgen, m);
		}
		System.out.println("+++++++++++++++++++++++++++++++++++");
		this.optimised = gen.getJavaClass();
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