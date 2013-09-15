package com.fasterxml.jackson.dataformat.smile;

import java.io.*;
import java.net.URL;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;

/**
 * Factory used for constructing {@link SmileParser} and {@link SmileGenerator}
 * instances; both of which handle
 * <a href="http://wiki.fasterxml.com/SmileFormat">Smile</a> encoded data.
 *<p>
 * Extends {@link JsonFactory} mostly so that users can actually use it in place
 * of regular non-Smile factory instances.
 *<p>
 * Note on using non-byte-based sources/targets (char based, like
 * {@link java.io.Reader} and {@link java.io.Writer}): these can not be
 * used for Smile-format documents, and thus will either downgrade to
 * textual JSON (when parsing), or throw exception (when trying to create
 * generator).
 * 
 * @author Tatu Saloranta
 */
public class SmileFactory extends JsonFactory
{
    private static final long serialVersionUID = -1696783009312472365L;

    /*
    /**********************************************************
    /* Constants
    /**********************************************************
     */
    
    /**
     * Name used to identify Smile format.
     * (and returned by {@link #getFormatName()}
     */
    public final static String FORMAT_NAME_SMILE = "Smile";
    
    /**
     * Bitfield (set of flags) of all parser features that are enabled
     * by default.
     */
    final static int DEFAULT_SMILE_PARSER_FEATURE_FLAGS = SmileParser.Feature.collectDefaults();

    /**
     * Bitfield (set of flags) of all generator features that are enabled
     * by default.
     */
    final static int DEFAULT_SMILE_GENERATOR_FEATURE_FLAGS = SmileGenerator.Feature.collectDefaults();

    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    /**
     * Whether non-supported methods (ones trying to output using
     * char-based targets like {@link java.io.Writer}, for example)
     * should be delegated to regular Jackson JSON processing
     * (if set to true); or throw {@link UnsupportedOperationException}
     * (if set to false)
     */
    protected boolean _cfgDelegateToTextual;

    protected int _smileParserFeatures;
    protected int _smileGeneratorFeatures;

    /*
    /**********************************************************
    /* Factory construction, configuration
    /**********************************************************
     */

    /**
     * Default constructor used to create factory instances.
     * Creation of a factory instance is a light-weight operation,
     * but it is still a good idea to reuse limited number of
     * factory instances (and quite often just a single instance):
     * factories are used as context for storing some reused
     * processing objects (such as symbol tables parsers use)
     * and this reuse only works within context of a single
     * factory instance.
     */
    public SmileFactory() { this(null); }

    public SmileFactory(ObjectCodec oc) {
        super(oc);
        _smileParserFeatures = DEFAULT_SMILE_PARSER_FEATURE_FLAGS;
        _smileGeneratorFeatures = DEFAULT_SMILE_GENERATOR_FEATURE_FLAGS;
    }

    /**
     * Note: REQUIRES 2.2.1 -- unfortunate intra-patch dep but seems
     * preferable to just leaving bug be as is
     * 
     * @since 2.2.1
     */
    public SmileFactory(SmileFactory src, ObjectCodec oc)
    {
        super(src, oc);
        _cfgDelegateToTextual = src._cfgDelegateToTextual;
        _smileParserFeatures = src._smileParserFeatures;
        _smileGeneratorFeatures = src._smileGeneratorFeatures;
    }

    // @since 2.1
    @Override
    public SmileFactory copy()
    {
        _checkInvalidCopy(SmileFactory.class);
        // note: as with base class, must NOT copy mapper reference
        return new SmileFactory(this, null);
    }
    
    public void delegateToTextual(boolean state) {
        _cfgDelegateToTextual = state;
    }

    /*
    /**********************************************************
    /* Serializable overrides
    /**********************************************************
     */

    /**
     * Method that we need to override to actually make restoration go
     * through constructors etc.
     * Also: must be overridden by sub-classes as well.
     */
    @Override
    protected Object readResolve() {
        return new SmileFactory(this, _objectCodec);
    }

    /*                                                                                       
    /**********************************************************                              
    /* Versioned                                                                             
    /**********************************************************                              
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************
    /* Format detection functionality
    /**********************************************************
     */
    
    @Override
    public String getFormatName() {
        return FORMAT_NAME_SMILE;
    }

    // Defaults work fine for this:
    // public boolean canUseSchema(FormatSchema schema) { }
    
    /**
     * Sub-classes need to override this method (as of 1.8)
     */
    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        return SmileParserBootstrapper.hasSmileFormat(acc);
    }

    /*
    /**********************************************************
    /* Capability introspection
    /**********************************************************
     */
    
    @Override
    public boolean canHandleBinaryNatively() {
        return true;
    }

    /*
    /**********************************************************
    /* Configuration, parser settings
    /**********************************************************
     */

    /**
     * Method for enabling or disabling specified parser feature
     * (check {@link SmileParser.Feature} for list of features)
     */
    public final SmileFactory configure(SmileParser.Feature f, boolean state)
    {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }

    /**
     * Method for enabling specified parser feature
     * (check {@link SmileParser.Feature} for list of features)
     */
    public SmileFactory enable(SmileParser.Feature f) {
        _smileParserFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified parser features
     * (check {@link SmileParser.Feature} for list of features)
     */
    public SmileFactory disable(SmileParser.Feature f) {
        _smileParserFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Checked whether specified parser feature is enabled.
     */
    public final boolean isEnabled(SmileParser.Feature f) {
        return (_smileParserFeatures & f.getMask()) != 0;
    }

    /*
    /**********************************************************
    /* Configuration, generator settings
    /**********************************************************
     */

    /**
     * Method for enabling or disabling specified generator feature
     * (check {@link SmileGenerator.Feature} for list of features)
     *
     * @since 1.2
     */
    public final SmileFactory configure(SmileGenerator.Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }


    /**
     * Method for enabling specified generator features
     * (check {@link SmileGenerator.Feature} for list of features)
     */
    public SmileFactory enable(SmileGenerator.Feature f) {
        _smileGeneratorFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified generator feature
     * (check {@link SmileGenerator.Feature} for list of features)
     */
    public SmileFactory disable(SmileGenerator.Feature f) {
        _smileGeneratorFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Check whether specified generator feature is enabled.
     */
    public final boolean isEnabled(SmileGenerator.Feature f) {
        return (_smileGeneratorFeatures & f.getMask()) != 0;
    }
    
    /*
    /**********************************************************
    /* Overridden parser factory methods, new (2.1)
    /**********************************************************
     */

    @SuppressWarnings("resource")
    @Override
    public SmileParser createParser(File f)
        throws IOException, JsonParseException
    {
        return _createParser(new FileInputStream(f), _createContext(f, true));
    }

    @Override
    public SmileParser createParser(URL url)
        throws IOException, JsonParseException
    {
        return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
    }

    @Override
    public SmileParser createParser(InputStream in)
        throws IOException, JsonParseException
    {
        return _createParser(in, _createContext(in, false));
    }

    //public JsonParser createJsonParser(Reader r)
    
    @Override
    public SmileParser createParser(byte[] data)
        throws IOException, JsonParseException
    {
        IOContext ctxt = _createContext(data, true);
        return _createParser(data, 0, data.length, ctxt);
    }
    
    @Override
    public SmileParser createParser(byte[] data, int offset, int len)
        throws IOException, JsonParseException
    {
        return _createParser(data, offset, len, _createContext(data, true));
    }
   
    /*
    /**********************************************************
    /* Overridden parser factory methods, old (pre-2.1)
    /**********************************************************
     */
    
    /**
     * @deprecated Since 2.1 Use {@link #createParser(File)} instead
     * @since 2.1
     */
    @SuppressWarnings("resource")
    @Deprecated
    @Override
    public SmileParser createJsonParser(File f)
        throws IOException, JsonParseException
    {
        return _createParser(new FileInputStream(f), _createContext(f, true));
    }

    /**
     * @deprecated Since 2.1 Use {@link #createParser(URL)} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileParser createJsonParser(URL url)
        throws IOException, JsonParseException
    {
        return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
    }

    /**
     * @deprecated Since 2.1 Use {@link #createParser(InputStream)} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileParser createJsonParser(InputStream in)
        throws IOException, JsonParseException
    {
        return _createParser(in, _createContext(in, false));
    }

    //public JsonParser createJsonParser(Reader r)
    
    /**
     * @deprecated Since 2.1 Use {@link #createParser(byte[])} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileParser createJsonParser(byte[] data)
        throws IOException, JsonParseException
    {
        IOContext ctxt = _createContext(data, true);
        return _createParser(data, 0, data.length, ctxt);
    }
    
    /**
     * @deprecated Since 2.1 Use {@link #createParser(byte[],int,int)} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileParser createJsonParser(byte[] data, int offset, int len)
        throws IOException, JsonParseException
    {
        return _createParser(data, offset, len, _createContext(data, true));
    }

    /*
    /**********************************************************
    /* Overridden generator factory methods, new (2.1)
    /**********************************************************
     */

    /**
     * Method for constructing {@link JsonGenerator} for generating
     * Smile-encoded output.
     *<p>
     * Since Smile format always uses UTF-8 internally, <code>enc</code>
     * argument is ignored.
     */
    @Override
    public SmileGenerator createGenerator(OutputStream out, JsonEncoding enc)
        throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        return _createGenerator(out, _createContext(out, false));
    }

    /**
     * Method for constructing {@link JsonGenerator} for generating
     * Smile-encoded output.
     *<p>
     * Since Smile format always uses UTF-8 internally, no encoding need
     * to be passed to this method.
     */
    @Override
    public SmileGenerator createGenerator(OutputStream out) throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        return _createGenerator(out, _createContext(out, false));
    }
    
    /*
    /**********************************************************
    /* Overridden generator factory methods, old (pre-2.1)
    /**********************************************************
     */
    
    /**
     * @deprecated Since 2.1 Use {@link #createGenerator(OutputStream)} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileGenerator createJsonGenerator(OutputStream out, JsonEncoding enc)
        throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        return _createGenerator(out, _createContext(out, false));
    }

    /**
     * @deprecated Since 2.1 Use {@link #createGenerator(OutputStream)} instead
     * @since 2.1
     */
    @Deprecated
    @Override
    public SmileGenerator createJsonGenerator(OutputStream out) throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        IOContext ctxt = _createContext(out, false);
        return _createGenerator(out, ctxt);
    }

    @Deprecated
    @Override
    protected SmileGenerator _createUTF8JsonGenerator(OutputStream out, IOContext ctxt)
        throws IOException
    {
        return _createGenerator(out, ctxt);
    }
    
    /*
    /******************************************************
    /* Overridden internal factory methods
    /******************************************************
     */

    //protected IOContext _createContext(Object srcRef, boolean resourceManaged)

    /**
     * Overridable factory method that actually instantiates desired
     * parser.
     */
    @Override
    protected SmileParser _createParser(InputStream in, IOContext ctxt)
        throws IOException, JsonParseException
    {
        return new SmileParserBootstrapper(ctxt, in).constructParser(_parserFeatures,
        		_smileParserFeatures, isEnabled(JsonFactory.Feature.INTERN_FIELD_NAMES),
        		_objectCodec, _rootByteSymbols);
    }

    /**
     * Overridable factory method that actually instantiates desired
     * parser.
     */
    @Override
    protected JsonParser _createParser(Reader r, IOContext ctxt)
        throws IOException, JsonParseException
    {
        if (_cfgDelegateToTextual) {
            return super._createParser(r, ctxt);
        }
        throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
    }

    /**
     * Overridable factory method that actually instantiates desired
     * parser.
     */
    @Override
    protected SmileParser _createParser(byte[] data, int offset, int len, IOContext ctxt)
        throws IOException, JsonParseException
    {
        return new SmileParserBootstrapper(ctxt, data, offset, len).constructParser(
                _parserFeatures, _smileParserFeatures,
                isEnabled(JsonFactory.Feature.INTERN_FIELD_NAMES),
                _objectCodec, _rootByteSymbols);
    }

    /**
     * Overridable factory method that actually instantiates desired
     * generator.
     */
    @Override
    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt)
        throws IOException
    {
        if (_cfgDelegateToTextual) {
            return super._createGenerator(out, ctxt);
        }
        throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
    }

    @Override
    protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        return _createGenerator(out, ctxt);
    }
    
    //public BufferRecycler _getBufferRecycler()

    @Override
    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException
    {
        if (_cfgDelegateToTextual) {
            return super._createWriter(out, enc, ctxt);
        }
        throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
    }
    
    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */
    
    protected SmileGenerator _createGenerator(OutputStream out, IOContext ctxt)
        throws IOException
    {
        int feats = _smileGeneratorFeatures;
        /* One sanity check: MUST write header if shared string values setting is enabled,
         * or quoting of binary data disabled.
         * But should we force writing, or throw exception, if settings are in conflict?
         * For now, let's error out...
         */
        SmileGenerator gen = new SmileGenerator(ctxt, _generatorFeatures, feats, _objectCodec, out);
        if ((feats & SmileGenerator.Feature.WRITE_HEADER.getMask()) != 0) {
            gen.writeHeader();
        } else {
            if ((feats & SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES.getMask()) != 0) {
                throw new JsonGenerationException(
                        "Inconsistent settings: WRITE_HEADER disabled, but CHECK_SHARED_STRING_VALUES enabled; can not construct generator"
                        +" due to possible data loss (either enable WRITE_HEADER, or disable CHECK_SHARED_STRING_VALUES to resolve)");
            }
            if ((feats & SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT.getMask()) == 0) {
        	throw new JsonGenerationException(
        			"Inconsistent settings: WRITE_HEADER disabled, but ENCODE_BINARY_AS_7BIT disabled; can not construct generator"
        			+" due to possible data loss (either enable WRITE_HEADER, or ENCODE_BINARY_AS_7BIT to resolve)");
            }
        }
        return gen;
    }
}
