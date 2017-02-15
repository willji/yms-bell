<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta charset="utf-8"> <!-- utf-8 works for most cases -->
        <meta name="viewport" content="width=device-width"> <!-- Forcing initial-scale shouldn't be necessary -->
        <meta http-equiv="X-UA-Compatible" content="IE=edge"> <!-- Use the latest (edge) version of IE rendering engine -->
        <title></title> <!-- The title tag shows in email notifications, like Android 4.4. -->

        <!-- Web Font / @font-face : BEGIN -->
        <!-- NOTE: If web fonts are not required, lines 9 - 26 can be safely removed. -->

        <!-- Desktop Outlook chokes on web font references and defaults to Times New Roman, so we force a safe fallback font. -->
        <!--[if mso]>
        <style>
            * {
                font-family: sans-serif !important;
            }
        </style>
        <![endif]-->
        <style type="text/css">
            table {
                border-collapse:collapse;
            }
            table, th, td {
                border: 1px solid black;
            }
            th, td {
                text-align: left;
            }
        </style>
    </head>
    <body>
        <table style="border-collapse: collapse; border: 1px solid black">
            <thead>
            <tr>
                <th style="border: 1px solid black">时间</th>
                <th style="border: 1px solid black">内容</th>
            </tr>
            </thead>
            <tbody>
            <#list histories as h>
                <tr>
                    <td style="border: 1px solid black">${h.timestamp?number_to_datetime}</td>
                    <td style="border: 1px solid black">${h.message}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </body>