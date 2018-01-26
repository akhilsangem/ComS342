  package varlang;
import static varlang.AST.*;
import static varlang.Value.*;

import java.util.List;
import java.util.ArrayList;

import varlang.AST.AddExp;
import varlang.AST.DecExp;
import varlang.AST.NumExp;
import varlang.AST.DivExp;
import varlang.AST.LeteExp;
import varlang.AST.MultExp;
import varlang.AST.Program;
import varlang.AST.SubExp;
import varlang.AST.VarExp;
import varlang.AST.Visitor;
import varlang.Env.EmptyEnv;
import varlang.Env.ExtendEnv;

public class Evaluator implements Visitor<Value> {
	
	Value valueOf(Program p) {
		Env env = new EmptyEnv();
		Env env1 = new ExtendEnv(env, "January", new NumVal(1));
		Env env2 = new ExtendEnv(env1, "February", new NumVal(2));
		Env env3 = new ExtendEnv(env2, "March", new NumVal(3));
		Env env4 = new ExtendEnv(env3, "April", new NumVal(4));
		Env env5 = new ExtendEnv(env4, "May", new NumVal(5));
		Env env6 = new ExtendEnv(env5, "June", new NumVal(6));
		Env env7 = new ExtendEnv(env6, "July", new NumVal(7));
		Env env8 = new ExtendEnv(env7, "August", new NumVal(8));
		Env env9 = new ExtendEnv(env8, "September", new NumVal(9));
		Env env10 = new ExtendEnv(env9, "October", new NumVal(10));
		Env env11 = new ExtendEnv(env10, "November", new NumVal(11));
		Env env12 = new ExtendEnv(env11, "December", new NumVal(12));
		
		// Value of a program in this language is the value of the expression
		return (Value) p.accept(this, env12);
	}
	
	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		return (Value) p.e().accept(this, env);
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}	

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<Value>(value_exps.size());
		
		for(Exp exp : value_exps) 
			values.add((Value)exp.accept(this, env));
		
		Env new_env = env;
		for (int i = 0; i < names.size(); i++)
			new_env = new ExtendEnv(new_env, names.get(i), values.get(i));

		return (Value) e.body().accept(this, new_env);		
	}

	@Override
	public Value visit(DecExp e, Env env) {
		
		return env.get(e.name());
	}

	@Override
	public Value visit(LeteExp e, Env env) {
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<Value>(value_exps.size());
		
		for(Exp exp : value_exps) 
			values.add((Value)exp.accept(this, env));
		
		Env new_env = env;
		for (int i = 0; i < names.size(); i++)
			new_env = new ExtendEnv(new_env, names.get(i), values.get(i));

		return (Value) e.body().accept(this, new_env);	
	}	
	
}
