package x10.lang;

@x10.core.X10Generated public class ArrayIndexOutOfBoundsException extends x10.lang.Exception implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ArrayIndexOutOfBoundsException.class);
    
    public static final x10.rtt.RuntimeType<ArrayIndexOutOfBoundsException> $RTT = x10.rtt.NamedType.<ArrayIndexOutOfBoundsException> make(
    "x10.lang.ArrayIndexOutOfBoundsException", /* base class */ArrayIndexOutOfBoundsException.class
    , /* parents */ new x10.rtt.Type[] {x10.lang.Exception.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ArrayIndexOutOfBoundsException $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ArrayIndexOutOfBoundsException.class + " calling"); } 
        x10.lang.Exception.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ArrayIndexOutOfBoundsException $_obj = new ArrayIndexOutOfBoundsException((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        
    }
    
    // constructor just for allocation
    public ArrayIndexOutOfBoundsException(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"
public ArrayIndexOutOfBoundsException() {super();
                                                                                                                                                                   {
                                                                                                                                                                      
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"

                                                                                                                                                                  }}
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"
public ArrayIndexOutOfBoundsException(final java.lang.String message) {super(((java.lang.String)(message)));
                                                                                                                                                                                                 {
                                                                                                                                                                                                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"

                                                                                                                                                                                                }}
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"
final public x10.lang.ArrayIndexOutOfBoundsException
                                                                                                                       x10$lang$ArrayIndexOutOfBoundsException$$x10$lang$ArrayIndexOutOfBoundsException$this(
                                                                                                                       ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/ArrayIndexOutOfBoundsException.x10"
return x10.lang.ArrayIndexOutOfBoundsException.this;
        }
        
        }
        