package cz.cvut.fit.gremlin;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Created on 4/19/2017.
 *
 * @author Marek.Cervak
 */
public class GraphQueryProcessor {
    public static void evaluate(CompiledScript compiledScript, Bindings bindings) throws ScriptException {
        System.out.println(compiledScript.eval(bindings));
    }

}
