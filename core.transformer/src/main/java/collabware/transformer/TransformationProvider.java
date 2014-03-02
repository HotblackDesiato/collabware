package collabware.transformer;


public interface TransformationProvider {

	Transformer createTransformer();
	Transformer createTransformer(PrecedenceRule precedence);

}
