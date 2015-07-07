/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package eu.tobby.momentanpol.objects;

import java.nio.Buffer;

import eu.tobby.momentanpol.utils.MeshObject;


public class Plane extends MeshObject
{
    // Data for drawing the 3D plane as overlay
    private static final double letterVertices[] = { -5.0f, -5.0f, 0.000000f, 5.0f, -5.0f, 0.000000f, 5.0f, 5.0f, 0.000000f, -5.0f, 5.0f, 0.000000f };
    
    private static final double letterNormals[] = { 0.000000f, 0.000000f, 1.000000f, 0.000000f, 0.000000f, 1.000000f, 0.000000f, 0.000000f, 1.000000f, 0.000000f, 0.000000f, 1.000000f };
    
    private static final double letterTexcoords[] = { 0.000000f, 0.000000f, 1.000000f, 0.000000f, 1.000000f, 1.000000f, 0.000000f, 1.000000f, };
    
    private static final short letterIndices[] = { 0, 1, 2, 0, 2, 3 };
    
    Buffer mVertBuff;
    Buffer mTexCoordBuff;
    Buffer mNormBuff;
    Buffer mIndBuff;

    // Size of the plane in surface Units
    float mHeight;
    float mWidth;
    
    
    public Plane()
    {
        mVertBuff = fillBuffer(letterVertices);
        mTexCoordBuff = fillBuffer(letterTexcoords);
        mNormBuff = fillBuffer(letterNormals);
        mIndBuff = fillBuffer(letterIndices);
    }

    public Plane(int width, int height)
    {
        mVertBuff = fillBuffer(letterVertices);
        mTexCoordBuff = fillBuffer(letterTexcoords);
        mNormBuff = fillBuffer(letterNormals);
        mIndBuff = fillBuffer(letterIndices);
        mWidth = width;
        mHeight = height;
    }
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
            default:
                break;
        }
        return result;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return letterVertices.length / 3;
    }
    
    
    @Override
    public int getNumObjectIndex()
    {
        return letterIndices.length;
    }
}
