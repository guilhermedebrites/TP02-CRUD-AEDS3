package app.arquivos;

import java.util.ArrayList;
import java.util.List;

import app.entidades.*;
import app.main.ArvoreBMais;

public class fileTarefa extends Arquivo<Tarefa> 
{
    ArvoreBMais<ParIDTarefa> arvore;

    public fileTarefa ( ) throws Exception {
        super( "Tarefas.db", Tarefa.class.getConstructor() );
        arvore = new ArvoreBMais<>( 
            ParIDTarefa.class.getConstructor(),
            5, 
            "Tarefas.db");
    }

    @Override
    public int create ( Tarefa obj ) throws Exception {
        int id = super.create( obj );
        try {
            arvore.create( new ParIDTarefa(obj.getIdCategoria(), obj.getId()) );
        } catch( Exception e ) {
            System.out.print( "" );
            e.printStackTrace( );
        }
        return id;
    }

    @Override
    public Tarefa read ( int idCategoria ) throws Exception {
        ArrayList<ParIDTarefa> picit = arvore.read( new ParIDTarefa(idCategoria, -1) );
        return super.read( picit.get(0).getIDTarefa() );
    }

    public List<Tarefa> readAll ( ) throws Exception {
        List<Tarefa> tarefas = new ArrayList<>( );

        file.seek(header);
        byte lapide = ' ';
        short tam = 0;
        byte[] b = null;

        Tarefa t = null;
        while( file.getFilePointer() < file.length() ){
            lapide = file.readByte();
            tam = file.readShort();
            b = new byte[tam];
            file.read(b);

            if( lapide != '*' ){
                t = new Tarefa();
                t.fromByteArray(b);
                tarefas.add(t);
            } 
        } 
        return ( tarefas );
    } 

    public List<Tarefa> readAll ( int idCategoria ) throws Exception {
        List<Tarefa> tarefas = new ArrayList<>( );

        file.seek(header);
        byte lapide = ' ';
        short tam = 0;
        byte[] b = null;

        Tarefa t = null;
        while( file.getFilePointer() < file.length() ) {
            lapide = file.readByte();
            tam = file.readShort();
            b = new byte[tam];
            file.read(b);

            if( lapide != '*' ){
                t = new Tarefa();
                t.fromByteArray(b);
                if( t.getIdCategoria() == idCategoria ){
                    tarefas.add(t);
                }
            }
        } 
        return ( tarefas );
    }
    
    @Override
    public boolean update ( Tarefa newTarefa ) throws Exception {
        boolean result = false;
        Tarefa oldTarefa = super.read( newTarefa.getId( ) );
        if(super.update(newTarefa)) {
            if( newTarefa.getId() != oldTarefa.getId() ) {
                arvore.delete( new ParIDTarefa(oldTarefa.getIdCategoria(), oldTarefa.getId()) );
                arvore.create( new ParIDTarefa(newTarefa.getIdCategoria(), newTarefa.getId()) );
            }
            result = true;
        }
        return result;
    }

    public boolean update (Tarefa newTarefa, int id) throws Exception {
        boolean result = false;
        Tarefa oldTarefa = super.read( newTarefa.getId( ) );
        if( super.update(newTarefa) ) {
            if( newTarefa.getIdCategoria() != oldTarefa.getIdCategoria() ) 
            {
                arvore.delete( new ParIDTarefa(oldTarefa.getIdCategoria(), oldTarefa.getId()) );
                arvore.create( new ParIDTarefa(newTarefa.getIdCategoria(), newTarefa.getId()) );
            }
            result = true;
        }
        return result;
    }

}