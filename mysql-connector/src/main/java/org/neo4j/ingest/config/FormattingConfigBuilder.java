package org.neo4j.ingest.config;

class FormattingConfigBuilder implements Formatting.Builder
{
    Delimiter delimiter = Delimiter.COMMA;
    Delimiter arrayDelimiter = Delimiter.SEMICOLON;
    QuoteChar quote = new QuoteChar( "\"" );

    @Override
    public Formatting.Builder delimiter( Delimiter delimiter )
    {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public Formatting.Builder arrayDelimiter( Delimiter delimiter )
    {
        this.arrayDelimiter = delimiter;
        return this;
    }

    @Override
    public Formatting.Builder quote( String quote )
    {
        this.quote = new QuoteChar( quote );
        return this;
    }

    @Override
    public Formatting build()
    {
        return new Formatting( this );
    }
}
