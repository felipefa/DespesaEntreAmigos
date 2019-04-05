package araujo.felipe.contajusta.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import araujo.felipe.contajusta.R;

/**
 * Classe responsável pela criação da Dialog com um "Sobre" a respeito do app,
 * onde são informados a descrição, o autor e a versão do app.
 */
public class DialogSobre {
    /**
     * Instancia uma nova dialog Sobre.
     *
     * @param contexto contexto (activity) em que a dialog deve ser exibida
     */
    public DialogSobre(Context contexto) {
        new AlertDialog.Builder(contexto)
                .setTitle(R.string.sobre)
                .setMessage(R.string.texto_sobre)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}
