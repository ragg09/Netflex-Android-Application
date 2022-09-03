<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Film_producer extends Model
{
    use HasFactory;
    protected $table = 'film_producer';
    protected $fillable = ['film_id','producer_id'];
    public $timestamps = FALSE;
}
