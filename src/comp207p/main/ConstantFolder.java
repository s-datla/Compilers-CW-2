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
	JavaClass optimized = null;

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
	
	private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method) {

		Code m = method.getCode();
		InstructionList ilist = new InstructionList(m.getCode());
		MethodGen mgen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), ilist,
			cpgen);
		for (InstructionHandle handle : ilist.getInstructionHandles()) {
			// if(handle.getInstruction() instanceof ICONST) {
				System.out.println(handle.getInstruction());
			// }
		}
	}

	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();

		ConstantPool cp = cpgen.getConstantPool();
		Constant[] constants = cp.getConstantPool();

		Method[] methods = cgen.getMethods();
		System.out.println("+++++++++++++++++++++++++++++++++++");
		System.out.println("Printing out methods!");
		for(Method m : methods) {
			optimizeMethod(cgen, cpgen, m);
		}
		System.out.println("+++++++++++++++++++++++++++++++++++");
		this.optimized = gen.getJavaClass();
	}

	
	public void write(String optimisedFilePath)
	{
		this.optimize();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimized.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}