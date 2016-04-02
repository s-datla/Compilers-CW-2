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

	// private Number handleArithmetic(InstructionList ilist, InstructionHandle handle) {

	// 	return 1;
	// }

	// // Get values from operations that generate values

	// // ArithmeticInstruction : 
	// // DADD, DDIV, DMUL, DNEG, DREM, DSUB, FADD, FDIV, FMUL, FNEG, FREM, FSUB, IADD, IAND, IDIV, IMUL, INEG, IOR, IREM, ISHL, ISHR, ISUB, IUSHR, IXOR, LADD, LAND, LDIV, LMUL, LNEG, LOR, LREM, LSHL, LSHR, LSUB, LUSHR, LXOR
	// // Comparisons :
	// // DCMPG, DCMPL, FCMPG, FCMPL, LCMP
	// // Constants And Pushes : 
	// // DCONST, FCONST, ICONST, LCONST, BIPUSH, SIPUSH
	// // ConversionInstruction :
	// // D2F, D2I, D2L, F2D, F2I, F2L, I2B, I2C, I2D, I2F, I2L, I2S, L2D, L2F, L2I

	// private Number getLatestValue(Instructionlist ilist, InstructionHandle handle) {
	// 	boolean foundLatest = false;
	// 	InstructionHandle nextHandle = handle.getPrev();
	// 	while(!foundLatest) {
	// 	}
	// }

	// private Boolean handleStore() {
	// 	return false;
	// }

	private int isInstruction(InstructionList ilist, InstructionHandle handle) {
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

	private void optimiseInstructions(ClassGen cgen, ConstantPoolGen cpgen, Method method) {

		Code m = method.getCode();
		InstructionList ilist = new InstructionList(m.getCode());
		MethodGen mgen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), ilist,
			cpgen);
		for (InstructionHandle handle : ilist.getInstructionHandles()) {
			int type = isInstruction(ilist, handle);
			// switch(type) {
			// 	case 1:
			// 	handleArithmetic(ilist,handle);
			// 	break;
			// 	case 3:
			// 	handleStore(ilist,handle);
			// 	break;
			// 	default:a
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