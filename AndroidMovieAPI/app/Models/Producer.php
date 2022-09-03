<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Producer extends Model
{
    use HasFactory;
    protected $table = 'producers';
    protected $fillable = ['name','email','website'];
    protected $primaryKey = 'id';
    public $timestamps = FALSE;
}
